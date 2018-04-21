var util = require('util');
var se = require('../ext/searchable-encryption-js/se');

exports.testEncryptDecrypt = function(test){
  
  var key = "sdiovjq387ghafivna";
  var text = "Hello World";
  
  var cipher = se.encrypt(key, text)
  var decipher = se.decrypt(key, cipher)
  
  test.equal(decipher, text, 'decipher: '+decipher +' text: '+text);
  test.done();
};

exports.testSearch = function(test){
  
  var key = "sdiovjq387ghafivna";
  var text = "Hello World";
  
  var cipher = se.encrypt(key, text)
  
  var e_keyword = se.encrypt_word(key, 'Hello')
  test.ok(se.search(cipher, e_keyword));
  
  var e_keyword = se.encrypt_word(key, 'World')
  test.ok(se.search(cipher, e_keyword));
  
  test.done();
};

var express = require("express");
var bodyParser     =   require("body-parser"); 
var app = express(); 
app.use(bodyParser.urlencoded({ extended: false }));  
var hostName = '127.0.0.1';
var port = 8090;

app.all('*', function(req, res, next) {  
  res.header("Access-Control-Allow-Origin", "*");  
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");  
  res.header("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");  
  res.header("X-Powered-By",' 3.2.1')  
  res.header("Content-Type", "application/json;charset=utf-8");  
  next(); 
});

var textMap = new Map();

app.get("/upload",function(req,res){
  //console.log("请求url：",req.path)
  console.log("请求参数：",req.query)
  var search_key = req.query.search_key;
  var abstract_1 = req.query.abstract_1;
  var abstract_2 = req.query.abstract_2;
  var abstract_3 = req.query.abstract_3;

  cipher_1 = se.encrypt(search_key, abstract_1);
  cipher_2 = se.encrypt(search_key, abstract_2);
  cipher_3 = se.encrypt(search_key, abstract_3);

  textMap.set('cipher_1', cipher_1);
  textMap.set('cipher_2', cipher_2);
  textMap.set('cipher_3', cipher_3);

  var result = {code:200,cipher_1:cipher_1,cipher_2:cipher_2,cipher_3:cipher_3};

  res.send(result);
})

app.get("/query",function(req,res){
  console.log("请求url：",req.path)
  console.log("请求参数：",req.query)
  var key_word = req.query.key_word;
  var key = req.query.search_key;

  var e_keyword = se.encrypt_word(key, key_word)

  var result_1 = se.search(textMap.get("cipher_1"), e_keyword);
  var result_2 = se.search(textMap.get("cipher_2"), e_keyword);
  var result_3 = se.search(textMap.get("cipher_3"), e_keyword);

  var result = {code:200,result_1:result_1,result_2:result_2,result_3:result_3};

  res.send(result);
})

app.post("/post",function(req,res){
  console.log("请求参数：",req.body);
  var result = {code:200,msg:"post请求成功"};
  res.send(result);
});


app.listen(port,hostName,function(){
  console.log(`服务器运行在http://${hostName}:${port}`);
});
