const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    app.use(
        '/openapi',
        createProxyMiddleware({
            target: 'http://openapi.data.go.kr',
            changeOrigin: true,
        })
    );
};