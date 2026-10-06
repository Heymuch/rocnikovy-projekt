.PHONY: get_stations
get_stations:
	@python3 get_stations.py;

.PHONY: get_railway_routes
get_railway_routes:
	@python3 get_railway_routes.py;

.PHONY: get_rails
get_rails:
	@python3 get_rails.py;

.PHONY: optimize_railway_rails
optimize_railway_rails:
	@python3 optimize_railway_rails.py;

.PHONY: station_rails_mapping
station_rails_mapping:
	@python3 station_rails_mapping.py;

.PHONY: rail_graph
rail_graph:
	@python3 rail_graph.py;

.PHONY: rail_segments
rail_segments:
	@python3 rail_segments.py;

.PHONY: station_segments
station_segments: rail_segments
	@python3 station_segments.py --max-distance 60;

.PHONY: station_path
station_path:
	@python3 station_path.py;
