select protocol_number
from protocol
where active = 'Y'
and update_timestamp >= to_date(?, 'yyyy-mm-dd hh24:mi:ss')
and update_timestamp <= to_date(?, 'yyyy-mm-dd hh24:mi:ss')
and protocol_type_code in (
    select protocol_type_code
    from protocol_type
    where description in (
        '118 Letter', 'Deferral of IRB Oversight', 'Determination of Human Research',
        'Emergency Use', 'Exempt', 'Exempt (2018 Rule)',
        'Expedite', 'Expedite (2018 Rule)', 'Full Committee',
        'Full Committee (2018 Rules)', 'Minimal Risk (2018 Rules)', 'Single Patient Use',
        'Standard', 'Humanitarian Use Device (HUD)'
    )
)
order by protocol_number
