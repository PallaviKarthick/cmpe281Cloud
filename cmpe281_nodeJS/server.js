var express = require('express');
var mongodb = require('mongodb');
var app = express();
var bodyParser = require('body-parser');
var MongoClient = require('mongodb').MongoClient;
var db;


app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


app.listen(9090);
console.log("Listening on port 9090");

//console.log(Math.floor(Number.MAX_SAFE_INTEGER * Math.random()));


var MongoClient = require('mongodb').MongoClient;
MongoClient.connect("mongodb://localhost:27017/test", function(err, database) {
    if (!err) {
        console.log("We are connected");
        db = database;
    }
});




//Blind GET request
app.get("/", function(req, res) {
    res.setHeader('Content-Type', 'application/json');
    res.send(JSON.stringify({ "use ": "/order" }));
});


// GET All Orders
app.get("/orders", function(req, res) {
    db.collection("starbucks").find({}, function(err, docs) {
        if (err) {
            res.status(500).json({
                message: 'Server Error'
            });
        }
        var orderArray = new Array();
        var temp = false;
        docs.each(function(err, doc) {
            if (doc) {
                orderArray.push(doc);
                //res.send(doc);
                temp = true;
            } else if (!doc && temp) {
                console.log(orderArray);
                res.send(orderArray);

            } else {
                res.status(400).json({
                    message: 'Orders not found'
                });
            }
        });

    });
});

// GET an Orders based on order_id
app.get("/order/:order_id", function(req, res) {
    db.collection("starbucks").findOne({ "id": req.params.order_id }, function(err, docs) {
        console.log(docs);
        if (err) {
            res.status(500).json({
                message: 'Server Error'
            });
        } else if (docs == null) {
            res.status(404).json({
                message: 'Order not found'
            });

        }
        res.send(docs);

    });
});

// Create an Orders 
app.post("/order", function(req, res) {
    console.log("---input---:" + req.body["location"]);
    if (req.body["location"] != null || req.body["location"] != undefined) {
        req.body.id = (Math.floor(Number.MAX_SAFE_INTEGER * Math.random())).toString();
        console.log(req.body);
        var full_address = req.protocol + "://" + req.headers.host + req.originalUrl;
        var order = setOrderStatus(req.body, full_address, "PLACED");

        db.collection("starbucks").insertOne(order);
        res.send(order);
    } else {
        res.status(400).json({
            message: 'Order is empty'
        });
    }
});

// Delete an Orders 
app.delete("/order/:order_id", function(req, res) {
    db.collection("starbucks").remove({ "id": req.params.order_id }, function(err, docs) {
        console.log("deleted order:" + docs);
        if (err) {
            res.status(500).json({
                message: 'Server Error'
            });
        }
        res.setHeader('Content-Type', 'application/json');
        res.send(JSON.stringify({ message: 'Successfully Cancelled the Order' }));

    });
});


// Update an Orders 
app.put("/order/:order_id", function(req, res) {
    var id = req.params.order_id;
    var ord = req.body;

    ord.id = id;
    var full_address = req.protocol + "://" + req.headers.host + req.originalUrl;
    var order = setOrderStatus(ord, full_address, "UPDATED");


    console.log('Updating order: ' + id);
    console.log(JSON.stringify(order));
    db.collection("starbucks", function(err, collection) {
        if (err) {
            console.log("--err-----" + err);
            res.status(500).json({
                message: 'Server Error'
            });

        }
        collection.update({ 'id': id }, order, { safe: true }, function(err, result) {
            if (err) {
                console.log('Error updating order: ' + err);
                res.status(500).json({
                    message: 'Server Error'
                });
            } else {
                console.log('' + result + ' document(s) updated');
                res.send(order);
            }
        });
    });



});


//Pay for the Order
app.post("/order/:order_id/pay", function(req, res) {
    var id = req.params.order_id;
    console.log("inside payment post");
    db.collection("starbucks").findOne({ "id": id }, function(err, docs) {
        console.log(docs);
        if (err) {
            res.status(404).json({
                message: 'Order Not Found'
            });
        } else if (docs == null) {
            res.status(404).json({
                message: 'Order not found'
            });

        }
        var ord = docs
        var full_address = req.protocol + "://" + req.headers.host + req.originalUrl;
        var order = setOrderStatus(ord, full_address, "PAID");
        console.log("--payed prder---" + JSON.stringify(order));


        db.collection("starbucks", function(err, collection) {
            if (err) {
                console.log("--err-----" + err);
                res.status(500).json({
                    message: 'Server Error'
                });

            }
            collection.update({ 'id': id }, order, { safe: true }, function(err, result) {
                if (err) {
                    console.log('Error updating order: ' + err);
                    res.status(500).json({
                        message: 'Server Error'
                    });
                } else {
                    console.log('' + result + ' document(s) updated');
                    res.send(order);

                    setTimeout(function() {
                        order.status = "PREPARING";
                        console.log("---startOrderProcessing--3-.1--" + order.status);
                        collection.update({ 'id': id }, order, { safe: true }, function(err, result) {
                            if (err) {
                                console.log('Error updating order: ' + err);
                                res.status(500).json({
                                    message: 'Server Error'
                                });
                            } else {
                                console.log('' + result + ' document(s) updated');
                                res.send(order);
                            }

                        });
                    }, 5000);


                    setTimeout(function() {

                        order.status = "SERVED";
                        console.log("---startOrderProcessing--3-.1--" + order.status);
                        collection.update({ 'id': id }, order, { safe: true }, function(err, result) {
                            if (err) {
                                console.log('Error updating order: ' + err);
                                res.status(500).json({
                                    message: 'Server Error'
                                });
                            } else {
                                console.log('' + result + ' document(s) updated');
                                res.send(order);
                            }

                        });
                    }, 50000);

                    setTimeout(function() {

                        order.status = "COLLECTED";
                        console.log("---startOrderProcessing--3-.1--" + order.status);
                        collection.update({ 'id': id }, order, { safe: true }, function(err, result) {
                            if (err) {
                                console.log('Error updating order: ' + err);
                                res.status(500).json({
                                    message: 'Server Error'
                                });
                            } else {
                                console.log('' + result + ' document(s) updated');
                                res.send(order);
                            }

                        });
                    }, 50000);


                }
            });
        });

    });

});



// function to update status
function setOrderStatus(doc, URI, status) {
    console.log("--doc---status-" + status);
    switch (status) {
        case "PLACED":
            doc.status = "PLACED";
            doc.message = "Order has been placed.";

            var map = {};
            map["order"] = URI + "/" + doc.id;
            map["payment"] = URI + "/" + doc.id + "/pay";
            doc.links = map;
            console.log("--doc----" + doc);
            break;

        case "UPDATED":
            doc.status = "UPDATED";
            doc.message = "order updated";
            var map = {};
            map["order"] = URI;
            map["payment"] = URI + "/pay";
            doc.links = map;
            break;

        case "PAID":
            doc.status = "PAID";
            doc.message = "Payment Accepted.";

            for (var key in doc.links) {
                console.log(' key=' + doc.links[key]);
                if (key == "payment") {
                    delete doc.links[key];
                }
            };

            break;
    }
    return doc;

}

function startOrderProcessing() {



}