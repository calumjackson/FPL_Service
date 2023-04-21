# Fantasy Premier League Application

Small applicaiton for playing around with kafka. 

Start kafka with 
> kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning; 

Start database with:
> sudo chmod -R 700 FplData
>
> pg_ctl -D FplData -l logfile start