/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import spark.Request;
import spark.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.cxf.attachment.Rfc5987Util;

import us.freeandfair.corla.controller.AuditReport;
import us.freeandfair.corla.util.SparkHelper;

/**
 * Download all of the data relevant to public auditing of a RLA.
 *
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class PublishAuditReport extends AbstractDoSDashboardEndpoint {
  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.GET;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/publish-audit-report";
  }

  /**
   * @return STATE authorization is necessary for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.STATE;
  }

  private String capitalize(final String string) {
    return string.substring(0,1).toUpperCase(Locale.US)
      + string.substring(1);
  }

  private String fileName(final String reportType, final String extension) {
    try {
      return Rfc5987Util.encode(String.format("%s_Report.%s",
                                              capitalize(reportType),
                                              extension),
                                "UTF-8");
    } catch(final UnsupportedEncodingException e) {
      return String.format("%s_Report.%s",
                           capitalize(reportType),
                           extension);
    }
 }

  /**
   * Download all of the data relevant to public auditing of a RLA.
   */
  @Override
  public String endpointBody(final Request request,
                             final Response response)  {
    String contentType;
    final String contestName = request.queryParams("contestName"); // optional when reportType is *-all
    final String reportType  = request.queryParams("reportType"); // activity/results
    final String reports  = request.queryParams("reports"); // report choices from UI popup

    List<String> selectedReports = Stream.of(reports.split(",", -1)).collect(Collectors.toList());

    contentType = request.queryParams("contentType");
    if (null == contentType) {
      contentType = request.headers("Accept"); //header wins
    }
    // todo ensure reportType is present

    byte[] reportBytes;
    try {

      final OutputStream os = SparkHelper.getRaw(response).getOutputStream();
      switch (contentType) {
      case "xlsx": case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
        reportBytes = AuditReport.generate("xlsx", reportType, contestName);
        response.header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.header("Content-Disposition", "attachment; filename*=UTF-8''" + fileName(reportType, "xlsx"));
        os.write(reportBytes);
        os.close();
        break;
      case "zip": case "application/zip":
        response.header("Content-Type", "application/zip");
        response.header("Content-Disposition", "attachment; filename*=UTF-8''" + fileName(reportType, "zip"));
        AuditReport.generateZip(os, selectedReports);
        os.close();
        break;
      default:
        invariantViolation(response, "Accept header or query param contentType is missing or invalid");
        return my_endpoint_result.get();
      }

      ok(response);
    } catch (final IOException e) {
      serverError(response, "Unable to stream response");
    }

    return my_endpoint_result.get();
  }
}
