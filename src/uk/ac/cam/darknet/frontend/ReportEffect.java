package uk.ac.cam.darknet.frontend;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.database.DatabaseManager;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * An effect for generating a report for each individual.
 * 
 * @author Johann Beleites
 * 
 */
public class ReportEffect extends Effect {
	public ReportEffect(DatabaseManager dm){
		super(dm);
	}
	
	@Override
	public void execute(Show show) {
		String path = "report.pdf";
		
		Document doc = new Document();
		try {
			PdfWriter.getInstance(doc, new FileOutputStream(path));
			doc.open();
			doc.add(new Paragraph("Hey space"));
		} catch (FileNotFoundException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			doc.close();
		}
		
		
		
	}
}