var mongoose = require('mongoose');

var orderSchema = mongoose.Schema({
     id:{type:String},
     location:{type:String},
     status:{type:String},
     message:{type:String}
});

var Order = module.exports = mongoose.model

//Get orders

module.exports.getOrder = function (callback,limit) {


  Order.find(callback).limit(limit);
}