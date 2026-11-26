import {
  AfterViewInit,
  Component,
  ElementRef,
  Injector,
  OnDestroy,
  ViewChild,
  effect,
  inject,
  input
} from '@angular/core';

import * as L from 'leaflet';

import {
  RoutePoint
} from 'src/app/commons/route-map/route-map.model';

@Component({
  selector: 'app-route-map',
  standalone: true,
  templateUrl: './route-map.html',
  styleUrl: './route-map.scss'
})
export class RouteMap implements AfterViewInit, OnDestroy {

  readonly routePoints = input<RoutePoint[]>([]);
  readonly stationFrom = input<string>('');
  readonly stationTo = input<string>('');

  @ViewChild('mapContainer')
  private mapContainer!: ElementRef<HTMLDivElement>;

  private readonly injector = inject(Injector);

  private map?: L.Map;
  private routeLayer?: L.LayerGroup;
  private resizeObserver?: ResizeObserver;

  ngAfterViewInit(): void {
    this.initMap();

    effect(() => {
      this.drawRoute(
        this.routePoints(),
        this.stationFrom(),
        this.stationTo()
      );
    }, {
      injector: this.injector
    });
  }

  private initMap(): void {

    this.map = L.map(
      this.mapContainer.nativeElement
    ).setView([49.8, 15.5], 7);

    // Základní mapový podklad
    L.tileLayer(
      'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        maxZoom: 19,
        attribution: '© OpenStreetMap contributors'
      }
    ).addTo(this.map);

    // Železniční síť
    L.tileLayer(
      'https://{s}.tiles.openrailwaymap.org/standard/{z}/{x}/{y}.png',
      {
        maxZoom: 19,
        attribution: '© OpenRailwayMap',
        opacity: 0.5
      }
    ).addTo(this.map);

    // Vrstva pro vlastní trasu a stanice
    this.routeLayer = L.layerGroup().addTo(this.map);

    this.resizeObserver = new ResizeObserver(() => {
      this.map?.invalidateSize();
    });

    this.resizeObserver.observe(
      this.mapContainer.nativeElement
    );
  }

  private drawRoute(
    routePoints: RoutePoint[],
    stationFrom: string,
    stationTo: string
  ): void {

    if (!this.map || !this.routeLayer) {
      return;
    }

    this.routeLayer.clearLayers();

    if (routePoints.length < 2) {
      this.map.setView([49.8, 15.5], 7);
      return;
    }

    const coordinates: L.LatLngTuple[] =
      routePoints.map(point => [
        point.latitude,
        point.longitude
      ]);

    // Bílý obrys trasy
    L.polyline(coordinates, {
      color: '#ffffff',
      weight: 9,
      opacity: 0.95,
      smoothFactor: 1,
      lineCap: 'round',
      lineJoin: 'round',
      interactive: false
    }).addTo(this.routeLayer);

    // Hlavní zvýrazněná trasa
    const polyline = L.polyline(coordinates, {
      color: '#00677b',
      weight: 5,
      opacity: 1,
      smoothFactor: 1,
      lineCap: 'round',
      lineJoin: 'round'
    }).addTo(this.routeLayer);

    const start = coordinates[0];
    const end = coordinates[coordinates.length - 1];

    // Výchozí stanice
    this.addStationMarker(start, stationFrom, '#2e7d32');

    // Cílová stanice
    this.addStationMarker(end, stationTo, '#c62828');

    this.map.invalidateSize();

    this.map.fitBounds(polyline.getBounds(), {
      padding: [50, 50],
      maxZoom: 15
    });
  }

  private addStationMarker(
    coordinates: L.LatLngTuple,
    stationName: string,
    color: string
  ): void {

    if (!this.routeLayer) {
      return;
    }

    const marker = L.circleMarker(coordinates, {
      radius: 8,
      color: '#ffffff',
      weight: 3,
      fillColor: color,
      fillOpacity: 1
    }).addTo(this.routeLayer);

    if (stationName) {
      const label = document.createElement('span');
      label.textContent = stationName;

      marker.bindTooltip(label, {
        permanent: true,
        direction: 'top',
        offset: [0, -10],
        opacity: 1
      });
    }
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
    this.map?.remove();
  }
}
