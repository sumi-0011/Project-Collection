import React, { Component } from 'react';
import axios from 'axios';

class image_page extends Component {
  constructor(props) {
    super(props)
    this.state = {
      file_name: '',
      file_base64: '',
      file_preview_url: '',
      detect_result: [],
    }
  }

  _handleFile(e) {
    let reader = new FileReader();
    reader.onloadend = () => {
      const base64 = reader.result;
      if (base64) {
        this.setState({
          file_base64: base64.toString().split(',')[1],
          file_preview_url: reader.result
        });
      }
    }
    if (e.target.files[0]) {
      reader.readAsDataURL(e.target.files[0]);
      this.setState({
        file_name: e.target.files[0].name
      });
    }
  }

  _detect = async function () {
    const { file_base64 } = this.state;

    if(file_base64 === ''){
      return alert('사진을 선택해주세요')
    }

    const detect_result = await axios('/detect/image', {
      method: 'POST',
      headers: new Headers(),
      data: { file_base64: file_base64 },
    })

    this.setState({
      detect_result: detect_result.data
    })
  }

  render() {
    let my_img = null;
    if (this.state.file_preview_url) {
      my_img = <img src={this.state.file_preview_url} alt='img' />
    }
    return (
      <div>
        {my_img}
        <label htmlFor='image'>
          사진 선택
        </label>
        <input type='file' id='image' accept='image/*' style={{display:'none'}} onChange={(e) => this._handleFile(e)} />
        <input type='button' value='사물인식' onClick={() => this._detect()} />
        {this.state.detect_result.length > 0
          ? this.state.detect_result.map((el, key) => {
            return (
              <div key={key}>
                {el}
              </div>
            )
          })
          : null
        }
      </div>
    )
  }
}

export default image_page;