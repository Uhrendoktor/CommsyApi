# CommsyApi
An unofficial API for SchulCommsy\
\
include commsyapi and https in your java project.\
\
Sample:\
\
CommsyClient client = new CommsyClient();\
try{\
  client.login(username, password);\
  System.out.println(String.format("Logged in: SID:%s PHPSESSID:%s UID:%s", client.SID, client.PHPSESSID, client.UID));\
}catch(CommsyInvalidLoginDataException e){\
  //Handle invalid login\
}catch(CommsyServerErrorException e){\
  //Handle Server error\
}\
\
DashboardRoom dashboard = client.rooms.get("Dashboard");\
DashboardFeed feed = dashboard.feed();\
Item[] items = feed.getItems(); //Items contain title, date, type, (author  or  time_date, time_from, time_to  <= if calendar entry)\
Items[] nextItems = feed.nextItems();\
\
Room room = items[0].room;\
Feed rfeed = room.feed();\
Item[] rItems = rfeed.getItems();\
