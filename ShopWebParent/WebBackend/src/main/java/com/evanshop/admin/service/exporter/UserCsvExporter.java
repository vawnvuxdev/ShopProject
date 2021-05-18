package com.evanshop.admin.service.exporter;

import com.evanshop.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserCsvExporter extends AbstractExporter {

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "users_","text/csv", ".csv");
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
				CsvPreference.STANDARD_PREFERENCE) ;
		
		String[] csvHeader = {"User ID", "First Name", "Last Name", "Email", "Roles", "Enabled"};
		String[] fieldMapping = {"id", "firstName", "lastName", "email", "roles", "enabled"};
		
		
		csvWriter.writeHeader(csvHeader);
		for(User u : listUsers) {
			csvWriter.write(u, fieldMapping);
		}
		
		csvWriter.close();
	}
	
}
