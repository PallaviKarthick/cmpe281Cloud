var mongoose = require('mongoose');

var orderItemSchema = mongoose.Schema({
     qty:{type:Integer},
     name:{type:String},
     milk:{type:String},
     size:{type:String}
});


