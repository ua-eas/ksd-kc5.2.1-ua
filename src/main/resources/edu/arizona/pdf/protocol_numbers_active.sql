select count(protocol_number)
from protocol p
    join protocol_type pt on p.protocol_type_code = pt.protocol_type_code
    join protocol_status ps on p.protocol_status_code = ps.protocol_status_code
where p.active = 'Y'
  and p.update_timestamp >= to_date(?, 'yyyy-mm-dd hh24:mi:ss')
  and p.update_timestamp <= to_date(?, 'yyyy-mm-dd hh24:mi:ss')
  and pt.description not in ('BAA','COI Management Plan','Data Use Agreement','Site Authorization')
  and ps.description not in ('Abandoned', 'Closed by Investigator', 'Deleted', 'Not Human Subjects Research',
                             'Recalled in Routing', 'Suspended by DSMB', 'Suspended by Investigator', 'Withdrawn')
order by protocol_number
