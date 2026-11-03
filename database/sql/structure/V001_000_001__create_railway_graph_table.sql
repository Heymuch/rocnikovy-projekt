CREATE TABLE railway_graph (
    point_a_id UUID NOT NULL,
    point_a_latitude FLOAT NOT NULL,
    point_a_longitude FLOAT NOT NULL,
    point_b_id UUID NOT NULL,
    point_b_latitude FLOAT NOT NULL,
    point_b_longitude FLOAT NOT NULL,
    distance INT NOT NULL
);
