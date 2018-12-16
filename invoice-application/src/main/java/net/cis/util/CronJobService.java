package net.cis.util;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import net.cis.service.InvoiceService;

/**
 * Created by NhanNguyen 19/10/2018
 */
public class CronJobService implements Job {
	
	@Autowired
	private InvoiceService invoiceService;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// Get failed invoice and re-created
		try {
			invoiceService.handleFailedInvoice();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
 