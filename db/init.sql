CREATE TABLE IF NOT EXISTS public.fpl_gameweeks
(
    manager_id integer NOT NULL,
    gameweek_id integer NOT NULL,
    season_id character varying(8) NOT NULL,
    week_points integer,
    bench_points integer,
    transfer_point_deductions integer,
    CONSTRAINT fpl_gameweeks_pkey PRIMARY KEY (manager_id, gameweek_id, season_id)
);

CREATE TABLE IF NOT EXISTS public.fpl_managers
(
    manager_id integer NOT NULL,
    first_name character varying(100),
    second_name character varying(100),
    team_name character varying(50) NOT NULL,
    CONSTRAINT fpl_manager_id_pk UNIQUE (manager_id)
)


CREATE TABLE IF NOT EXISTS public.fpl_players
(
    player_id integer NOT NULL,
    player_first_name character varying(100),
    player_second_name character varying(100),
    team_id integer NOT NULL,
    position_id integer NOT NULL,
    selected_by_percent float NOT NULL,
    player_value INTEGER NOT NULL,
    player_position character varying(100),

    CONSTRAINT fpl_player_id_pk UNIQUE (player_id)
)

CREATE TABLE IF NOT EXISTS public.fpl_teams
(
    team_id integer,
    team_name character varying(100) COLLATE pg_catalog."default",
    CONSTRAINT fpl_team_id_pk UNIQUE (team_id)
)