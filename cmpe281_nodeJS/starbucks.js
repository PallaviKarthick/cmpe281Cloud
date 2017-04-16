var express = require('express');
var mongodb = require('mongodb');
var app = express();
var bodyParser = require('body-parser');
var MongoClient = require('mongodb').MongoClient;
var db;


app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
// Initialize connection once
MongoClient.connect("mongodb://nikhitha1234:jamjam123@cluster0-shard-00-00-tzslu.mongodb.net:27017,cluster0-shard-00-01-tzslu.mongodb.net:27017,cluster0-shard-00-02-tzslu.mongodb.net:27017/hello?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&readPreference=secondary", function(err, database) {
  if(err) throw err;

  db = database;

  // Start the application after the database connection is ready
  app.listen(8080);
  console.log("Listening on port 8080");
});

// Reuse database object in request handlers
app.get("/", function(req, res) {
  db.collection("starbucks").find({}, function(err, docs) {
    docs.each(function(err, doc) {
      if(doc) {
        console.log(doc);
      }
      else {
        res.end();
      }
    });
  });
});

app.post("/", function(req, res) {
	console.log(req.body)
	db.collection("starbucks").insertOne(req.body);
});