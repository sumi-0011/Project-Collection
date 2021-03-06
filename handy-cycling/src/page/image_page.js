import React, { Component } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

class image_page extends Component {
  constructor(props) {
    super(props);
    this.state = {
      file_name: "",
      file_base64: "",
      file_preview_url: "",
      detect_result: [],
    };
  }

  _handleFile(e) {
    let reader = new FileReader();
    reader.onloadend = () => {
      const base64 = reader.result;
      if (base64) {
        this.setState({
          file_base64: base64.toString().split(",")[1],
          file_preview_url: reader.result,
        });
      }
    };
    if (e.target.files[0]) {
      reader.readAsDataURL(e.target.files[0]);
      this.setState({
        file_name: e.target.files[0].name,
      });
    }
  }

  _detect = async function () {
    const { file_base64 } = this.state;

    if (file_base64 === "") {
      return alert("사진을 선택해주세요");
    }

    const detect_result = await axios("/detect/image", {
      method: "POST",
      headers: new Headers(),
      data: { file_base64: file_base64 },
    });

    this.setState({
      detect_result: detect_result.data,
    });
  };

  render() {
    let my_img = null;
    if (this.state.file_preview_url) {
      my_img = (
        <img
          style={{ height: "100%" }}
          src={this.state.file_preview_url}
          alt="img"
        />
      );
    }
    return (
      <div>
        {this.state.file_preview_url === "" ? (
          <div>
            <label htmlFor="image">
              <div
                style={{
                  color: "white",
                  backgroundColor: "black",
                  opacity: "0.5",
                  width: window.innerWidth / 2,
                  height: window.innerWidth / 3,
                  marginTop:'30px'
                }}
              >
                사진을 선택해 주세요.
              </div>
            </label>
          </div>
        ) : (
          <div
            style={{
              width: window.innerWidth / 2,
              height: window.innerWidth / 3,
              margin: "0 auto",
            }}
          >
            <label
              style={{
                width: window.innerWidth / 2,
                height: window.innerWidth / 3,
                margin: "0 auto",
              }}
              htmlFor="image"
            >
              {my_img}
            </label>
          </div>
        )}
        <input
          type="file"
          id="image"
          accept="image/*"
          style={{ display: "none" }}
          onChange={(e) => this._handleFile(e)}
        />
        <input
          style={{
            width: "250px",
            height: "50px",
            fontSize: "30px",
            fontWeight: "bold",
            borderRadius: "20px",
            marginTop: "10px",
            backgroundColor: "#A9F5A9",
            marginBottom: "30px",
          }}
          type="button"
          value="확인하기"
          onClick={() => this._detect()}
        />
        {this.state.detect_result.length > 0
          ? this.state.detect_result.map((el, key) => {
              return (
                <div key={key}>
                  <span
                    style={{
                      width: "250px",
                      height: "50px",
                      fontSize: "30px",
                      fontWeight: "bold",
                      marginTop: "30px",
                      color:'black',
                      marginRight:'20px'
                    }}
                  >
                  {el}
                  </span>
                  <Link
                    to={{
                      pathname: `/info/${`Styrofoam`}`,
                      state: {
                        type: 'Styrofoam',
                      },
                    }}
                    style={{
                      width: "250px",
                      height: "50px",
                      fontSize: "30px",
                      fontWeight: "bold",
                      borderRadius: "20px",
                      marginTop: "30px",
                      backgroundColor: "#A9F5A9",
                      textDecoration: 'unset',
                      color:'black'
                    }}
                  >
                    스티로폼 배출방법 보기
                  </Link>
                  <div>
                 
                  </div>
                </div>
              );
            })
          : null}
      </div>
    );
  }
}

export default image_page;
