import React from "react";
import * as UpdateConfirmStateDB from "../backend/ConfirmStateAPI.js";

function ConfirmeState() {

  const onSubmit = (e) => {
    e.preventDefault();
    UpdateConfirmStateDB.ConfirmStateAPI();
  }

  return (
    <div>
      <button
        onClick={onSubmit}>
          update
      </button>
    </div>
  );
    
}

export default ConfirmeState;