select protocol_number
from protocol
where active = 'Y'
and update_timestamp >= to_date('?', 'yyyy-mm-dd hh24:mi:ss')
and update_timestamp <= to_date('?', 'yyyy-mm-dd hh24:mi:ss')
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
and (
    (protocol_status_code in (
        select protocol_status_code
        from protocol_status
        where description in (
            'Active - Closed to Enrollment', 'Active - Data Analysis Only',
            'Active - Open to Enrollment', 'Deferred',
            'Exempt', 'IRB review not required',
            'Suspended by IRB')
        and protocol_status_code in (
            select protocol_status_code from protocol_submission where submission_status_code in (
                select submission_status_code from submission_status where description = 'Approved')))
    )
    or
    (protocol_status_code in (
        select protocol_status_code
        from protocol_status
        where description in (
            'Closed Administratively for lack of response', 'Disapproved',
            'Terminated by PI')
        and protocol_status_code in (
            select protocol_status_code from protocol_submission where submission_status_code in (
                select submission_status_code from submission_status where description = 'Closed')))
    )
    or
    (protocol_status_code in (
        select protocol_status_code
        from protocol_status
        where description in (
            'Amendment in Progress', 'Pending/In Progress',
            'Renewal in Progress', 'Return To PI',
            'Specific Minor Revisions Required', 'Submitted to IRB',
            'Substantive Revisions Required', 'Suspended by DSMB')
        )
    )
)
