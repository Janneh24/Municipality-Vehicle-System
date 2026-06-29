package it.unifi.swam.parking.rest;

import it.unifi.swam.parking.model.Violation;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.OutputStream;

@RequestScoped
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {

    @PersistenceContext(unitName = "parkingPU")
    private EntityManager em;

    @GET
    @Path("/violations")
    public Response getViolationReport(
            @QueryParam("period") @DefaultValue("all") String period,
            @QueryParam("groupBy") @DefaultValue("district") String groupBy) {

        System.out.println("Generating violation report. Period: " + period + ", GroupBy: " + groupBy);

        LocalDateTime since = null;
        LocalDateTime now = LocalDateTime.now();
        if ("day".equalsIgnoreCase(period)) {
            since = now.minusDays(1);
        } else if ("week".equalsIgnoreCase(period)) {
            since = now.minusWeeks(1);
        } else if ("month".equalsIgnoreCase(period)) {
            since = now.minusMonths(1);
        }

        String jpql;
        if ("type_and_engine".equalsIgnoreCase(groupBy)) {
            jpql = "SELECT CONCAT(v.vehicleType, ' / ', v.engineType), COUNT(v) FROM Violation v";
        } else {
            String groupByField = "district";
            if ("vehicleType".equalsIgnoreCase(groupBy)) {
                groupByField = "vehicleType";
            } else if ("engineType".equalsIgnoreCase(groupBy)) {
                groupByField = "engineType";
            }
            jpql = "SELECT v." + groupByField + ", COUNT(v) FROM Violation v";
        }

        if (since != null) {
            jpql += " WHERE v.timestamp >= :since";
        }

        if ("type_and_engine".equalsIgnoreCase(groupBy)) {
            jpql += " GROUP BY v.vehicleType, v.engineType";
        } else {
            String groupByField = "district";
            if ("vehicleType".equalsIgnoreCase(groupBy)) {
                groupByField = "vehicleType";
            } else if ("engineType".equalsIgnoreCase(groupBy)) {
                groupByField = "engineType";
            }
            jpql += " GROUP BY v." + groupByField;
        }

        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        if (since != null) {
            query.setParameter("since", since);
        }

        List<Object[]> results = query.getResultList();
        Map<String, Long> report = new HashMap<>();

        for (Object[] row : results) {
            String key = (String) row[0];
            Long count = (Long) row[1];
            report.put(key, count);
        }

        return Response.ok(report).build();
    }

    @GET
    @Path("/violations/user/{userId}")
    public Response getViolationsByUser(@PathParam("userId") Long userId) {
        System.out.println("DEBUG: Fetching violations for userId: " + userId);
        try {
            // Find violations where license plate belongs to the user
            TypedQuery<Violation> query = em.createQuery(
                    "SELECT v FROM Violation v WHERE v.licensePlate IN (SELECT veh.licensePlate FROM Vehicle veh WHERE veh.owner.id = :userId) ORDER BY v.timestamp DESC",
                    Violation.class);
            query.setParameter("userId", userId);
            List<Violation> violations = query.getResultList();
            return Response.ok(violations).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/violations/pdf")
    @Produces("application/pdf")
    public Response getViolationPdf() {
        StreamingOutput fileStream = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws java.io.IOException, WebApplicationException {
                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, output);
                    document.open();

                    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                    Paragraph title = new Paragraph("Municipality Motor Vehicle System - Violation Report", titleFont);
                    title.setAlignment(Element.ALIGN_CENTER);
                    document.add(title);
                    document.add(new Paragraph("Generated on: " + LocalDateTime.now().toString()));
                    document.add(new Paragraph(" "));

                    // Summary Table
                    PdfPTable table = new PdfPTable(2);
                    table.setWidthPercentage(100);
                    table.addCell("Category");
                    table.addCell("Violation Count");

                    TypedQuery<Object[]> query = em.createQuery(
                            "SELECT v.district, COUNT(v) FROM Violation v GROUP BY v.district", Object[].class);
                    List<Object[]> results = query.getResultList();

                    for (Object[] row : results) {
                        table.addCell((String) row[0]);
                        table.addCell(row[1].toString());
                    }

                    document.add(table);

                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Detailed Violations List:"));

                    PdfPTable detailsTable = new PdfPTable(4);
                    detailsTable.setWidthPercentage(100);
                    detailsTable.addCell("License Plate");
                    detailsTable.addCell("District");
                    detailsTable.addCell("Type");
                    detailsTable.addCell("Date");

                    TypedQuery<Violation> vQuery = em.createQuery("SELECT v FROM Violation v ORDER BY v.timestamp DESC",
                            Violation.class);
                    List<Violation> violations = vQuery.getResultList();

                    for (Violation v : violations) {
                        detailsTable.addCell(v.getLicensePlate());
                        detailsTable.addCell(v.getDistrict());
                        detailsTable.addCell(v.getVehicleType());
                        detailsTable.addCell(v.getTimestamp().toString());
                    }

                    document.add(detailsTable);
                    document.close();
                } catch (Exception e) {
                    throw new WebApplicationException(e);
                }
            }
        };

        return Response.ok(fileStream)
                .header("Content-Disposition", "attachment; filename=\"violation-report.pdf\"")
                .build();
    }

    @POST
    @Path("/violations/test")
    @jakarta.transaction.Transactional
    public Response createMockViolation(java.util.Map<String, String> data) {
        System.out.println("DEBUG: createMockViolation called with data: " + data);
        try {
            Violation violation = new Violation();
            violation.setLicensePlate(data.get("licensePlate"));
            violation.setDistrict(data.get("district"));
            violation.setVehicleType(data.get("vehicleType"));
            violation.setEngineType(data.get("engineType"));
            violation.setTimestamp(java.time.LocalDateTime.now());

            em.persist(violation);
            System.out.println("DEBUG: Mock violation persists successfully for plate: " + violation.getLicensePlate());
            return Response.status(Response.Status.CREATED).entity(violation).build();
        } catch (Exception e) {
            System.err.println("DEBUG ERROR in createMockViolation: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
}