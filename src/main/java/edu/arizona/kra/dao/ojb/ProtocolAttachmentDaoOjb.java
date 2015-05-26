package edu.arizona.kra.dao.ojb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.springmodules.orm.ojb.OjbFactoryUtils;

import edu.arizona.kra.dao.ProtocolAttachmentDao;

public class ProtocolAttachmentDaoOjb extends PlatformAwareDaoBaseOjb implements ProtocolAttachmentDao {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger( ProtocolAttachmentDaoOjb.class );

	private enum SqlType {
		SELECT, UPDATE
	};

	public byte[] getAttachmentData( Long fileId ) {
		String query = "SELECT file_data FROM attachment_file WHERE file_id = ?";
		Object[] params = new Object[] { fileId };
		String[] returnColumns = new String[] { "file_data" };

		validateParams( new String[] { "File ID" }, params );

		List<List<Object>> results = prepareAndExecuteStatement( query, params, returnColumns );
		for ( List<Object> resultRow : results ) {
			if ( resultRow.size() > 0 && resultRow.get( 0 ) instanceof byte[] ) {
				return (byte[]) resultRow.get( 0 );
			}
		}

		return null;
	}

	public void saveAttachmentData( Long fileId, byte[] attachmentData ) {
		String query = "UPDATE attachment_file SET file_data = ? WHERE file_id = ?";
		Object[] params = new Object[] { attachmentData, fileId };

		validateParams( new String[] { "File ID", "File Data" }, params );

		prepareAndExecuteStatement( query, params );
	}

	public ArrayList<Long> checkForNullFileData( Long protocolId ) {
		ArrayList<Long> nullFileIds = new ArrayList<Long>();

		String query = "SELECT file_id FROM attachment_file WHERE file_data IS NULL " +
				"AND file_id IN (SELECT file_id FROM protocol_attachment_protocol WHERE protocol_id_fk = ?)";
		Object[] params = new Object[] { protocolId };
		String[] columns = new String[] { "file_id" };

		validateParams( new String[] { "Protocol ID" }, params );

		List<List<Object>> results = prepareAndExecuteStatement( query, params, columns );
		for ( List<Object> resultRow : results ) {
			if ( resultRow.size() > 0 && resultRow.get( 0 ) instanceof Long ) {
				nullFileIds.add( (Long) resultRow.get( 0 ) );
			}
		}

		return nullFileIds;
	}

	protected void validateParams( String[] names, Object[] queryParams ) {
		if ( queryParams.length != names.length ) {
			throw new IllegalArgumentException( "Incorrect number of arguments" );
		}

		for ( int i = 0 ; i < queryParams.length ; i++ ) {
			if ( queryParams[i] == null ) {
				throw new IllegalArgumentException( "The parameter " + names[i] + " cannot be null" );
			}
		}
	}

	// Convenience methods
	protected List<List<Object>> prepareAndExecuteStatement( String sql, Object[] queryParams, String[] returnValues ) {
		return prepareAndExecuteStatement( sql, queryParams, returnValues, SqlType.SELECT );
	}

	protected List<List<Object>> prepareAndExecuteStatement( String sql, Object[] queryParams ) {
		return prepareAndExecuteStatement( sql, queryParams, null, SqlType.UPDATE );
	}

	protected List<List<Object>> prepareAndExecuteStatement( String sql, Object[] queryParams, String[] returnValues, SqlType sqlType ) {
		PersistenceBroker broker = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;

		List<List<Object>> rows = null;
		List<Object> columns = null;
		try {
			broker = getPersistenceBroker( false );
			conn = broker.serviceConnectionManager().getConnection();

			stmt = conn.prepareStatement( sql );

			for ( int i = 0 ; i < queryParams.length ; i++ ) {
				if ( queryParams[i] == null ) {
					stmt.setNull( i + 1, Integer.MIN_VALUE );
				}
				/*
				 * else if (queryParams[i] instanceof byte[]) { stmt.setBytes(i + 1, (byte[]) queryParams[i]); }
				 */
				else {
					stmt.setObject( i + 1, queryParams[i] );
				}
			}

			if ( sqlType == SqlType.SELECT ) {
				rs = stmt.executeQuery();

				rows = new ArrayList<List<Object>>();
				columns = new ArrayList<Object>();

				try {
					while ( rs.next() ) {
						for ( String columnName : returnValues ) {
							ResultSetMetaData metadata = rs.getMetaData();
							int columnIndex = rs.findColumn( columnName );
							if ( metadata.getColumnType( columnIndex ) == Types.BLOB ) {
								columns.add( rs.getBytes( columnIndex ) );
							}
							else {
								columns.add( rs.getObject( columnIndex ) );
							}
						}
						rows.add( columns );
					}
				} finally {
					try {
						rs.close();
					} catch ( Exception ignore ) {
					}
				}
			}
			else {
				stmt.executeUpdate();
			}
		} catch ( SQLException sqle ) {
			LOG.error( "SQLException: " + sqle.getMessage(), sqle );
			throw new WorkflowRuntimeException( sqle );
		} catch ( LookupException le ) {
			LOG.error( "LookupException: " + le.getMessage(), le );
			throw new WorkflowRuntimeException( le );
		} finally {
			try {
				if ( stmt != null ) {
					stmt.close();
				}
				if ( broker != null ) {
					OjbFactoryUtils.releasePersistenceBroker( broker, this.getPersistenceBrokerTemplate().getPbKey() );
				}
			} catch ( Exception e ) {
				LOG.error( "Failed closing connection: " + e.getMessage(), e );
			}
		}
		return rows;
	}

}