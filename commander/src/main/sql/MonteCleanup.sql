/* 
Script for checking big values on Monte's install 
*/

/**  account, mtu, start time, end time, threshold

call cleanEnergyData(234, 1248452, (1434412440-300), (1438190580 + 3600), 1000);
*/



select  mtu_id, timestamp, CONVERT_TZ(FROM_UNIXTIME(timestamp),'SYSTEM', 'US/Eastern'), Hex(mtu_id), energyDifference, energy from energydata 
/*where account_id = 242   /* MONTE*/ 
where account_id = 52 /* PETE*/ 
and timestamp > 1446847811
and (HEX(mtu_id) like ('16FFFA%') or HEX(mtu_id) like ('160114%'))
order by energyDifference desc
limit 100;

/*
select  mtu_id, timestamp, CONVERT_TZ(FROM_UNIXTIME(timestamp),'SYSTEM', 'US/Eastern'), Hex(mtu_id), energyDifference, energy from energydata 
where account_id = 52 
and mtu_id = 385874444
and (timestamp > 1446549420 - 600)
and (timestamp < 1446549420 + 3600)
order by timestamp 
*/



/*
update energydata
set avg15=11.3611
where 
avg15 < -1000 
and account_id = 242 
and mtu_id=1445078
and timestamp >= (1438486800 - 3600) and timestamp <= (1438486800+4800);



select * from energydata 
where account_id = 242 
and mtu_id=1445078
and timestamp >= (1438486800 - 3600) and timestamp <= (1438486800+4800)
order by mtu_id, timestamp

*/
