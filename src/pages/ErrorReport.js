import React from "react";
import '../css/ErrorReport.css';
import { useForm } from "react-hook-form";
import * as SendError from "../backend/SendError.js";

function ErrorReport() {
  const { register, handleSubmit } = useForm();
  const onSubmit = (title, report) => {
    SendError.SendError(title, report);
    alert("전송되었습니다!");
    document.getElementById('inputReportTitle').value = "";
    document.getElementById('inputReportContent').value = "";
  };
  return (
    <div id="main-wrapper">
      <div id="error-report-container">
        <div className="container">
          {/* <h1>ErrorReport</h1> */}
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="form-group">
              <label htmlFor="inputReportTitle">title</label>
              <input
                className="form-control"
                type="text"
                placeholder="report title input"
                id="inputReportTitle"
                {...register("inputReportTitle")}
              />
            </div>
            <div className="form-group">
              <label htmlFor="inputReportContent">report</label>
              <textarea
                className="form-control"
                id="inputReportContent"
                rows="3"
                placeholder="report error input"
                {...register("inputReportContent")}
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
