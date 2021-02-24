package edu.arizona.kra.irb.pdf.excel;


import edu.arizona.kra.irb.pdf.sql.enums.Column;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExcelRowMapper implements RowMapper<ExcelAttachmentRecord> {
    @Override
    public ExcelAttachmentRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExcelAttachmentRecord record = new ExcelAttachmentRecord();
        record.setId(rs.getString(Column.id.name()));
        record.setDestType(rs.getString(Column.dest_type.name()));
        record.setProtocolNumber(rs.getString(Column.protocol_number.name()));
        record.setHuronDestination(rs.getString(Column.huron_destination.name()));
        record.setDestAttrIsSet(Integer.parseInt(rs.getString(Column.dest_att_is_set.name())));
        record.setUiFilename(rs.getString(Column.ui_filename.name()));
        record.setSftpPath(rs.getString(Column.sftp_path.name()));
        record.setCategory(rs.getString(Column.category.name()));
        record.setVersionId(rs.getString(Column.version_id.name()));
        record.setIsProcessed(rs.getString(Column.is_processed.name()));
        record.setError(rs.getString(Column.error.name()));
        record.setDocumentId(rs.getString(Column.document_id.name()));

        return record;
    }
}
