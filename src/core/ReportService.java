package core;

import dao.AppointmentDAO;
import java.util.List;

/** FR15: daily + weekly appointment summaries. */
public class ReportService {
    private final AppointmentDAO dao = new AppointmentDAO();

    public List<String[]> dailyReport() { return dao.dailyReport(); }
    public List<String[]> weeklyReport() { return dao.weeklyReport(); }
}