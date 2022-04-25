/* To generate the report of active pages along with their properties */
import java.text.SimpleDateFormat;
import com.day.cq.dam.api.AssetManager;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

def df = new SimpleDateFormat("dd/MM/yyyy");
def replicator = getService("com.day.cq.replication.Replicator")
assetManager = resourceResolver.adaptTo(AssetManager)

def path = "/content/DAM"

HSSFWorkbook workbook = new HSSFWorkbook();
sheet = workbook.createSheet("Sheet 1");
HSSFCellStyle cellStyle = workbook.createCellStyle();
cellStyle.setWrapText(true);
HSSFCellStyle my_style = workbook.createCellStyle();
HSSFFont my_font = workbook.createFont();

my_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
my_font.setColor(HSSFColor.WHITE.index);
my_style.setFont(my_font);
my_style.setFillBackgroundColor(HSSFColor.BLUE.index);
my_style.setAlignment(CellStyle.ALIGN_CENTER);
my_style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
my_style.setFillPattern(CellStyle.SOLID_FOREGROUND);

HSSFRow rowhead = sheet.createRow(0);

def columnCount = 0;

Cell cell = rowhead.createCell(columnCount++);
cell.setCellValue("SL No.");
cell.setCellStyle(my_style);

cell = rowhead.createCell(columnCount++);
cell.setCellValue("Page path");
cell.setCellStyle(my_style);

cell = rowhead.createCell(columnCount++);
cell.setCellValue("Page Title");
cell.setCellStyle(my_style);

cell = rowhead.createCell(columnCount++);
cell.setCellValue("Activation Date");
cell.setCellStyle(my_style);

HSSFRow row;
rowNum = 1;

getPage(path).recurse { page ->

	def replicationStatus = replicator.getReplicationStatus(session, page.path)
	if(replicationStatus.isActivated()) {
		row = sheet.createRow((short) (rowNum));
		def colCount = 0;
		
		row.createCell(colCount++).setCellValue(rowNum);
		row.createCell(colCount++).setCellValue(page.path);
		row.createCell(colCount++).setCellValue(page.title);
		row.createCell(colCount++).setCellValue(replicationStatus.getLastPublished().getTime().toString());
		
		rowNum++;
	}
}

while(columnCount>0){
	sheet.autoSizeColumn(columnCount--);
}


Date date = new Date();
SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'T'HH-mm-ss");
def dateTimeString = dateFormat.format(date);
def filename = "Report_"+dateTimeString+".xls";

createDamFileForExcel(workbook, filename);

// method to create spreadsheet asset in dam
def createDamFileForExcel(def workbook, def filename) {
	def baos = new ByteArrayOutputStream();
	workbook.write(baos);
	def location = "/content/dam/groovy/output/"
	def bais = new ByteArrayInputStream(baos.toByteArray());
	def asset = assetManager.createAsset(location + filename, bais, "application/vnd.ms-excel", true);
	bais.close();
	baos.close();
	location + filename
}