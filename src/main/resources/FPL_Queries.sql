select * 
from fpl_players

;

select players.*, teams.team_name 
from fpl_players players
left outer join fpl_teams teams
on players.team_id = teams.team_id
WHERE selected_by_percent > 10
order by selected_by_percent desc
;

create table transfer_histories (
    item_id character varying(30) not null,
    player_id INTEGER not null,
    transfers_in INTEGER not null,
    transfers_in_event INTEGER not null,
    transfers_out INTEGER not null,
    transfers_out_event INTEGER not null,
    update_time timestamp not null,
    CONSTRAINT fpl_transfer_histories_pk UNIQUE (item_id)

);

select * from transfer_histories;

delete from transfer_histories;