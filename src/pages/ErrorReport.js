import React from "react";
import '../css/ErrorReport.css';
function ErrorReport() {
  return (
    <div id="main-wrapper">
      <div id="error-report-container">
        <div className="container">
          {/* <h1>ErrorReport</h1> */}
          <form>
            <div className="form-group">
              <label htmlFor="inputReportTitle">title</label>
              <input
                className="form-control"
                type="text"
                placeholder="report title input"
                id="inputReportTitle"
              />
            </div>
            <div className="form-group">
              <label htmlFor="inputReportContent">report</label>
              <textarea
                className="form-control"
                id="inputReportContent"
                rows="3"
                placeholder="report error input"
              ></textarea>
            </div>
            <button type="submit" class="btn btn-primary">
              Submit
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default ErrorReport;
