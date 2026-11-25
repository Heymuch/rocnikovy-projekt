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

    L.tileLayer(
      'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        maxZoom: 19,
        attribution: '© OpenStreetMap contributors'
      }
    ).addTo(this.map);

    L.tileLayer(
      'https://{s}.tiles.openrailwaymap.org/standard/{z}/{x}/{y}.png',
      {
        maxZoom: 19,
        attribution: '© OpenRailwayMap'
      }
    ).addTo(this.map);

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

    const polyline = L.polyline(coordinates, {
      color: '#1976d2',
      weight: 5,
      opacity: 0.9,
      smoothFactor: 1
    }).addTo(this.routeLayer);

    const start = coordinates[0];
    const end = coordinates[coordinates.length - 1];

    this.addStationMarker(start, stationFrom, '#2e7d32');
    this.addStationMarker(end, stationTo, '#c62828');

    this.map.invalidateSize();

    this.map.fitBounds(polyline.getBounds(), {
      padding: [30, 30],
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
      radius: 7,
      color: '#ffffff',
      weight: 2,
      fillColor: color,
      fillOpacity: 1
    }).addTo(this.routeLayer);

    if (stationName) {
      const label = document.createElement('span');
      label.textContent = stationName;
      marker.bindTooltip(label);
    }
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
    this.map?.remove();
  }
}
