select distinct PROTOCOL_NUMBER from protocol
where PROTOCOL_NUMBER in ('0700000468','0600000430','0900000065')
and update_timestamp >= to_date(?, 'yyyy-mm-dd hh24:mi:ss')
and update_timestamp <= to_date(?, 'yyyy-mm-dd hh24:mi:ss')