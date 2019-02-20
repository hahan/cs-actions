package io.cloudslang.content.excel.actions;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static io.cloudslang.content.excel.utils.Constants.BAD_CREATE_EXCEL_FILE_MSG;
import static io.cloudslang.content.excel.utils.Constants.EXCEPTION_WORKSHEET_NAME_EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by alexandra boicu 20/2/2019
 */
public class NewExcelDocumentTest {
    public static final String FILE_NAME = System.getProperty("java.io.tmpdir")
            + "testFile2.xls";
    public static final String BAD_EXT_FILE_NAME = System.getProperty("java.io.tmpdir")
            + "testFile2.jpg";

    private static NewExcelDocument toTest;

    @Before
    public void setUp() throws IOException {
        toTest = new NewExcelDocument();

        File f = new File(FILE_NAME);
        if (f.exists()) {
            f.delete();
        }
    }

    @After
    /**
     * delete the xml document that was created at setUp.
     */
    public void CleanUp() {
        File f = new File(FILE_NAME);
        f.delete();
    }

    @Test
    /**
     * test functionality of execute method.
     * @throws Exception
     */
    public void testExecute() throws Exception {
        Map<String, String> result = toTest.execute(FILE_NAME, "sheet1,sheet12", ",");

        assertEquals("0", result.get("returnCode"));

        File f = new File(FILE_NAME);
        assertTrue(f.exists());
        FileInputStream fis = new FileInputStream(f);
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        assertNotNull(workbook);
        HSSFSheet sheet = workbook.getSheet("sheet1");
        assertNotNull(sheet);
        sheet = workbook.getSheet("sheet12");
        assertNotNull(sheet);
    }

    @Test
    /**
     * test execute method with Filename input to an existing file.
     * @throws Exception
     */
    public void testExecute2() throws Exception {

        File f = new File(FILE_NAME);
        if (!f.exists()) {
            f.createNewFile();
        }
        Map<String, String> result = toTest.execute(FILE_NAME, "sheet1,sheet12", ",");
        f.delete();

        assertEquals("-1", result.get("returnCode"));
        assertEquals("File already exists", result.get("returnResult"));
    }

    @Test
    /**
     * test execute method with Filename input with bad extension.
     * @throws Exception
     */
    public void testExecute3() {
        Map<String, String> result = toTest.execute(BAD_EXT_FILE_NAME, "sheet1,sheet12", ",");
        assertEquals("-1", result.get("returnCode"));
        assertEquals(BAD_CREATE_EXCEL_FILE_MSG, result.get("returnResult"));
    }

    @Test
    /**
     * test execute method with empty sheetnames.
     * this will create default "sheet1, sheet2, sheet3".
     * @throws Exception
     */
    public void testExecute4() {
        Map<String, String> result = toTest.execute(FILE_NAME, "", "");
        assertEquals("0", result.get("returnCode"));
        assertTrue(result.get("returnResult").toString().contains("created successfully"));
    }

    @Test
    /**
     * test execute method with empty sheetnames.
     * @throws Exception
     */
    public void testExecute5() {
        Map<String, String> result = toTest.execute(FILE_NAME, ",", ",");
        assertEquals("-1", result.get("returnCode"));
        assertEquals(EXCEPTION_WORKSHEET_NAME_EMPTY, result.get("returnResult"));
    }
}
