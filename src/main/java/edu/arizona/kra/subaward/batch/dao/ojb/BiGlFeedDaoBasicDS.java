package edu.arizona.kra.subaward.batch.dao.ojb;

import edu.arizona.kra.subaward.batch.bo.BiGlEntry;
import edu.arizona.kra.subaward.batch.dao.BiGLFeedDao;
import edu.arizona.kra.util.BiDBConnection;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiDecimalFieldConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.*;


/**
 * Created by nataliac on 8/3/18.
 */
public class BiGlFeedDaoBasicDS implements BiGLFeedDao {
    private static final Logger LOG = LoggerFactory.getLogger(BiGLFeedDao.class);

    protected DataSource dataSource;


    public List<BiGlEntry> importGLData(Date beginDate, Date endDate) throws SQLException,LookupException {
        LOG.info("BiGlFeedDaoBasicDS:importGLData Start ");
        long startTime = System.currentTimeMillis();

        List <BiGlEntry> results = new ArrayList<BiGlEntry>();

        String sqlQuery = BI_GL_TABLE_SELECT_QUERY1 + beginDate.toString()+TIMESTAMP_ZERO +BI_GL_TABLE_SELECT_QUERY2 + endDate.toString()+TIMESTAMP_EOD+BI_GL_TABLE_SELECT_QUERY3;

        try (BiDBConnection dbc = new BiDBConnection(getDataSource()) ) {

            //Not working: Timestamp[] params = {new Timestamp(beginDate.getTime()), new Timestamp(endDate.getTime())};
           //Not working String[] params = { beginDate.toString()+TIMESTAMP_ZERO, endDate.toString()+TIMESTAMP_ZERO };
            ResultSet rs = dbc.executeQuery(sqlQuery, null);

           while (rs.next()) {
               BiGlEntry glEntry = new BiGlEntry();

               glEntry.setEntryId(rs.getLong(COL_GL_ENTRY_ID));
               glEntry.setAccountNumber(rs.getString(COL_GL_ACCOUNT_NBR));
               glEntry.setUniversityFiscalYear(rs.getInt(COL_GL_UNIV_FISCAL_YR));
               glEntry.setChartOfAccountsCode(rs.getString(COL_GL_FIN_COA_CD));
               glEntry.setSubAccountNumber(rs.getString(COL_GL_SUB_ACCT_NBR));
               glEntry.setFinancialObjectCode(rs.getString(COL_GL_FIN_OBJECT_CD));
               glEntry.setFinancialSubObjectCode(rs.getString(COL_GL_FIN_SUB_OBJ_CD));
               glEntry.setFinancialBalanceTypeCode(rs.getString(COL_GL_FIN_BALANCE_TYP_CD));
               glEntry.setFinancialObjectTypeCode(rs.getString(COL_GL_FIN_OBJ_TYP_CD));
               glEntry.setUniversityFiscalPeriodCode(rs.getString(COL_GL_UNIV_FISCAL_PRD_CD));
               glEntry.setFinancialDocumentTypeCode(rs.getString(COL_GL_FDOC_TYP_CD));
               glEntry.setFinancialSystemOriginationCode(rs.getString(COL_GL_FS_ORIGIN_CD));
               glEntry.setDocumentNumber(rs.getString(COL_GL_FDOC_NBR));
               glEntry.setTransactionLedgerEntrySequenceNumber(rs.getInt(COL_GL_TRN_ENTR_SEQ_NBR));
               glEntry.setTransactionLedgerEntryDescription(rs.getString(COL_GL_TRN_LDGR_ENTR_DESC));

               //big decimal
               OjbKualiDecimalFieldConversion decimalConverter = new OjbKualiDecimalFieldConversion();
               glEntry.setTransactionLedgerEntryAmount((KualiDecimal)decimalConverter.sqlToJava(rs.getObject(COL_GL_TRN_LDGR_ENTR_AMT)));

               glEntry.setTransactionDebitCreditCode(rs.getString(COL_GL_TRN_DEBIT_CRDT_CD));
               glEntry.setTransactionDate(rs.getDate(COL_GL_TRANSACTION_DT));
               glEntry.setOrganizationDocumentNumber(rs.getString(COL_GL_ORG_DOC_NBR));
               glEntry.setProjectCode(rs.getString(COL_GL_PROJECT_CD));
               glEntry.setOrganizationReferenceId(rs.getString(COL_GL_ORG_REFERENCE_ID));
               glEntry.setReferenceFinancialDocumentTypeCode(rs.getString(COL_GL_FDOC_REF_TYP_CD));
               glEntry.setReferenceFinancialSystemOriginationCode(rs.getString(COL_GL_FS_REF_ORIGIN_CD));
               glEntry.setReferenceFinancialDocumentNumber(rs.getString(COL_GL_FDOC_REF_NBR));
               glEntry.setFinancialDocumentReversalDate(rs.getDate(COL_GL_FDOC_REVERSAL_DT));
               glEntry.setTransactionEncumbranceUpdateCode(rs.getString(COL_GL_TRN_ENCUM_UPDT_CD));
               glEntry.setTransactionPostingDate(rs.getDate(COL_GL_TRN_POST_DT));
               glEntry.setTransactionDateTimeStamp(rs.getTimestamp(COL_GL_TIMESTAMP));

               results.add(glEntry);
            }
        }catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw sqle;
        }

        long elapsed = System.currentTimeMillis() - startTime;

        LOG.info("BiGlFeedDaoBasicDS:importGLData duration: "+ elapsed + " ms");
        return results;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
