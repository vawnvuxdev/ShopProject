package com.evanshop.admin.service.exporter;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.evanshop.common.entity.Category;

public class CategoryCsvExporter extends AbstractExporter {
	
	public void CsvExport(HttpServletResponse response, List<Category> listCategories) throws IOException {
		super.setResponseHeader(response, "categories_", "text/csv", ".csv");
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
				CsvPreference.STANDARD_PREFERENCE) ;
		
		String[] csvHeader = {"Category ID", "Category Name"};
		String[] fieldMapping = {"id", "name"};
		
		
		csvWriter.writeHeader(csvHeader);
		
		for(Category c : listCategories) {
			csvWriter.write(c, fieldMapping);
		}
		
		csvWriter.close();
	}
}
