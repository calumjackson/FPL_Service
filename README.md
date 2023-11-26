# Fantasy Premier League Application

Small applicaiton for playing around with kafka. 

Start kafka with 
> kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning; 

Start database with:
> sudo chmod -R 700 FplData
>
> pg_ctl -D FplData -l logfile start


### Kakfa Listener Information
https://www.confluent.io/en-gb/blog/kafka-client-cannot-connect-to-broker-on-aws-on-docker-etc/
https://www.confluent.io/en-gb/blog/kafka-listeners-explained/

### Useful logs
/var/log/cloud-init-output.log


The environement setup for kafka took some points:
* define the listeners in general - the container can resolve itself in this case (to broker or whatever the container is identified as )
* Listener security map - still not sure what this does entirely.
* Define the internal listener so that it points to the listener with resolvable refernces.
* Define the advertised listeners, which is what the broker will respond to the client with the details of which endpoint to connect to. 
* The advertised listener will need a resolveable IP from the internet. For EC2, this will likely need an elastic IP address as the IPs will change when the EC2 instance is stopped & restated.

> environment:
>       KAFKA_BROKER_ID: 1
>       KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
>      KAFKA_LISTENERS: PLAINTEXT_EXTERNAL://broker:9092,PLAINTEXT_INTERNAL://broker:29092
>      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT_EXTERNAL://ec2-3-10-167-78.eu-west-2.compute.amazonaws.com:9092,PLAINTEXT_INTERNAL://broker:29092
>       KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT_EXTERNAL:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
>       KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
>       KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
>       KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
>      KAFKA_HEAP_OPTS: "-Xmx512m -Xms512M"
>      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT_INTERNAL


Copy data from local drive to AWS.
> # scp -i <certificate> -r <file_to_upload> <target-user>@<IP>:\<file_locations>
> scp -i AWS_Coding/AWS_Tutorial/cj-tutorial.pem -r FPL_Service ec2-user@3.10.167.78:\data



'''
select * from fpl_teams;
select * from fpl_players;


select players.*, teams.team_name 
from fpl_players players
left outer join fpl_teams teams
on players.team_id = teams.team_id
WHERE selected_by_percent > 10
order by selected_by_percent desc
;
'''

