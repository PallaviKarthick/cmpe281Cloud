// Update an Orders 
app.put("/order/:order_id", function(req, res) {
  db.collection("starbucks").findOne({"id": req.params.order_id}, function(err, doc) {
    if (err)
    res.send(err);
    order = req.body;
    doc.status="UPDATED";
    doc.message="order updated";
    console.log("--after -doc---",doc);

    doc.Update(function(err){
    if(err)
      res.send(err);
   res.setHeader('Content-Type', 'application/json');
   res.send(JSON.stringify({  message: 'Successfully Updated' }));

    });


 

});
});