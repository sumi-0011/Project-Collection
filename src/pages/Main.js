import React from "react";
// import
function Main() {
  return (
    <div>
      <Tempelate />
    </div>
  );
}

function Tempelate() {
  return (
    <div id="page-wrapper">
      {/* <!-- Banner --> */}
      {/* <Banner /> */}
      {/* <!-- Features --> */}
      {/* <Features /> */}
      {/* <!-- Main --> */}
      <MainContainer />
    </div>
  );
}
function MainContainer() {
  return (
    <div id="main-wrapper">
      <div className="container">
        <div className="row gtr-200">
          <div className="col-4 col-12-medium">
            {/* <!-- Sidebar --> */}
            <div id="sidebar">
              <section className="widget thumbnails">
                <h3>Interesting stuff</h3>
                <div className="grid">
                  <div className="row gtr-50">
                    <div className="col-6">
                      <a href="#" className="image fit">
                        <img src="images/pic04.jpg" alt="" />
                      </a>
                    </div>
                    <div className="col-6">
                      <a href="#" className="image fit">
                        <img src="images/pic05.jpg" alt="" />
                      </a>
                    </div>
                    <div className="col-6">
                      <a href="#" className="image fit">
                        <img src="images/pic06.jpg" alt="" />
                      </a>
                    </div>
                    <div className="col-6">
                      <a href="#" className="image fit">
                        <img src="images/pic07.jpg" alt="" />
                      </a>
                    </div>
                  </div>
                </div>
                <a href="#" className="button icon fa-file-alt">
                  More
                </a>
              </section>
            </div>
          </div>
          <div className="col-8 col-12-medium imp-medium">
            {/* <!-- Content --> */}
            <div id="content">
              <section className="last">
                <h2>So what's this all about?</h2>
                <p>
                  This is <strong>Verti</strong>, a free and fully responsive
                  HTML5 site template by{" "}
                  <a href="http://html5up.net">HTML5 UP</a>. Verti is released
                  under the{" "}
                  <a href="http://html5up.net/license">
                    Creative Commons Attribution license
                  </a>
                  , so feel free to use it for any personal or commercial
                  project you might have going on (just don't forget to credit
                  us for the design!)
                </p>
                <p>
                  Phasellus quam turpis, feugiat sit amet ornare in, hendrerit
                  in lectus. Praesent semper bibendum ipsum, et tristique augue
                  fringilla eu. Vivamus id risus vel dolor auctor euismod quis
                  eget mi. Etiam eu ante risus. Aliquam erat volutpat. Aliquam
                  luctus mattis lectus sit amet phasellus quam turpis.
                </p>
                <a href="#" className="button icon solid fa-arrow-circle-right">
                  Continue Reading
                </a>
              </section>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}


function Banner() {
  return (
    <div id="banner-wrapper">
      <div id="banner" className="box container">
        <div className="row">
          <div className="col-7 col-12-medium">
            <h2>Hi. This is Verti.</h2>
            <p>It's a free responsive site template by HTML5 UP</p>
          </div>
          <div className="col-5 col-12-medium">
            <ul>
              <li>
                <a
                  href="#"
                  className="button large icon solid fa-arrow-circle-right"
                >
                  Ok let's go
                </a>
              </li>
              <li>
                <a
                  href="#"
                  className="button alt large icon solid fa-question-circle"
                >
                  More info
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}

function Features() {
  return (
    <div id="features-wrapper">
      <div className="container">
        <div className="row">
          <div className="col-4 col-12-medium">
            {/* <!-- Box --> */}
            <section className="box feature">
              <a href="#" className="image featured">
                <img src="images/pic01.jpg" alt="" />
              </a>
              <div className="inner">
                <header>
                  <h2>Put something here</h2>
                  <p>Maybe here as well I think</p>
                </header>
                <p>
                  Phasellus quam turpis, feugiat sit amet in, hendrerit in
                  lectus. Praesent sed semper amet bibendum tristique fringilla.
                </p>
              </div>
            </section>
          </div>
          <div className="col-4 col-12-medium">
            {/* <!-- Box --> */}
            <section className="box feature">
              <a href="#" className="image featured">
                <img src="images/pic02.jpg" alt="" />
              </a>
              <div className="inner">
                <header>
                  <h2>An interesting title</h2>
                  <p>This is also an interesting subtitle</p>
                </header>
                <p>
                  Phasellus quam turpis, feugiat sit amet in, hendrerit in
                  lectus. Praesent sed semper amet bibendum tristique fringilla.
                </p>
              </div>
            </section>
          </div>
          <div className="col-4 col-12-medium">
            {/* <!-- Box --> */}
            <section className="box feature">
              <a href="#" className="image featured">
                <img src="images/pic03.jpg" alt="" />
              </a>
              <div className="inner">
                <header>
                  <h2>Oh, and finally ...</h2>
                  <p>Here's another intriguing subtitle</p>
                </header>
                <p>
                  Phasellus quam turpis, feugiat sit amet in, hendrerit in
                  lectus. Praesent sed semper amet bibendum tristique fringilla.
                </p>
              </div>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
}


export default Main;
