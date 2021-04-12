select distinct protocol_number
from protocol p
join protocol_type pt on p.protocol_type_code = pt.protocol_type_code
join protocol_status ps on p.protocol_status_code = ps.protocol_status_code
where p.active = 'Y'
  and pt.description not in ('Business Associate Agreement','COI Management Plan','Data Use Agreement','Site Authorization')
  and ps.description not in ('Abandoned', 'Closed by Investigator', 'Deleted', 'Not Human Subjects Research',
                             'Recalled in Routing', 'Suspended by DSMB', 'Suspended by Investigator', 'Withdrawn')
order by protocol_number