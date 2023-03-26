
# Store Monitoring

Loop monitors several restaurants in the US and needs to monitor if the store is online or not. All restaurants are supposed to be online during their business hours. Due to some unknown reasons, a store might go inactive for a few hours. Restaurant owners want to get a report of the how often this happened in the past.   

We want to build backend APIs that will help restaurant owners achieve this goal.

## APIs
1. `GET /get-report/{reportId}`
It will download the csv file for given report id.

2. `GET /trigger-report/{restoId}` 
It will generate the report for resto and return report Id.

3. `POST /add-menu` RequestBody:
`{"storeId":1481966498820158979,"day":3,"startTimeLocal":"00:00:00","endTimeLocal":"00:10:00"}`
This api will add single menu-hours entity to Database.

4. `GET /add-menu-from-csv/{csv-path}`
It will read the csv file(of format `store_id,day,start_time_local,end_time_local`) and store the data into database. For example if you have placed `Menu-hours.csv` file at the root of the project than api will be `/add-menu-from-csv/Menu-hours`  It will also return in response the first 10 records from the csv, with some success or error message.

5. `POST /add-status` RequestBody:`{"storeId":"8419537941919820732","status":"active","timeStampUTC":"2023-01-22T12:09:39.388884"}`
This api will add status of a given restaurant in db. Note here DateTime format is `yyyy-mm-dd'T'hh:mm:ss.SSSSSS` This is in UTC. 

6. `GET /add-status-from-csv/{csv-path}` It will read the csv from the root of folder, the format is `store_id,status,timestamp_utc`. Note here the timestamp is in format of `yyyy-mm-dd hh:mm:ss.SSSSSS UTC`. It will also return the first 10 records , error message , success.

7. `POST /add-restaurant-timezone` RequestBody:```{"storeId":"8139926242460185114","timezoneStr":"Asia/Beirut"}``` Adds timezone for single restaurantId.

8. `GET /add-timezone-from-csv/{csv-path}` Add csv file to db, csv of format `store_id,timezone_str` also return first 10 records and error or success message.


## Steps to run the application
1. install maven.
2. git clone the project.
3. add csv files to the root of project.
4. Start mysql database and add DB properties to application.properties file present resources folder.
4. Run command: ```mvn spring-boot:run```. It will use embeded tomcat server and will run on port 8080.

## Logic (while generating a report):
1. find the latest timestamp for a resto in store status table.
2. Take last 7 days timestamps in decreasing order by timestamps, why last 7 days because we need to answer lastweek uptime and downtime.
3. Now out of all these time stamps reject all those which are not between the business hours,after considering conversion of timezones.
4. Now sort the remaining timestamps in decreasing order of time stamp so that we can use them to calculate uptime and downtimes of hour, day and week
5. calculate uptime of last hour by taking only those time stamps which were present with in 1 hour difference from extreme timestamp; Then: divide the hour into time slots of size `precision_time` lets say 15 mins, then extract minutes value from each time stamp and poll according to slot value , lets say minutes was 22 than it falls in second slot of hour [15-30). hence `we consider that if atleast 1 timestamp is present in any slot then that slot will be considered as uptime` so for 22 minutes we will say that store was having uptime of 15 mins starting from 15 to 29 both inclusive.
Finally all the slots which had atleast 1 time stamp are added to give the uptime of last hour.

6. For calculating last day uptime we pick the latest time stamp (which is present within business hours considering timezone) lets say `2023-01-22T12:09:39.388884` then we will pick `2023-01-22T00:00` as base timestamp(because it is the last day) and will consider all those timestamps which are present between these two timestamps and will do hour wise polling(like we did for last hour uptime using precision time and time slots, here also we will consider 15 mins as precision time). adding timeslots for each hour where ever there was a time stamp present. 

7. For calculating last week uptime lets say latest time stamp was `2023-01-22T12:09:39.388884` and it was wednesday then we will take base time stamp as `2023-01-20T00:00` i,e Monday Midnight as base, and then calculate day uptimes for Monday, Tuesday and wednesday timestamps and sum them up. (for uptime of individual day we use the process same as in the previous step lastuptime for day).

8. For Calculating the last hour downtime we subtract uptime from 60 mins.

9. for calculating the last day downtime, we subtract uptime from total available Business time (from Menu-hours for that particular day.) For ex: for monday(as per latest Day timestamp) business hours were from 10AM to 12PM. Then from 120 minutes we will subtract lastday uptime.

10. for calculating last week downtime get the monday midnight timestamp and calculate total business hours of each day which falls between latest and base timestamps. from total business hours subtract last week uptime in minutes.










