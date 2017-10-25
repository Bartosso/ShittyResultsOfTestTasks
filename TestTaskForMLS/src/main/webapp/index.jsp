<%--
  Created by IntelliJ IDEA.
  User: Eshu
  Date: 23.10.2017
  Time: 11:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
      <script src="http://code.jquery.com/jquery-latest.min.js"></script>
      <script src="jquery.tablesorter.js"></script>
      <link rel="stylesheet" type="text/css" href="tablesorterTheme/style.css">
    <title>Test task for Bartosso</title>

  </head>
  <body>
  <form id="filterform" method="post" action="/mainservlet/">
      <table width="100%" cellspacing="0" cellpadding="4">
          <tr>
              <td align="center"><b>Filter</b></td>
          </tr>
          <tr>
              <td align="left" width="100">PN</td>
              <td><input type="text" name="PN" maxlength="50" size="45"></td>
          </tr>
          <tr>
              <td align="left">Part Name</td>
              <td><input type="text" name="PartName" maxlength="50" size="45"></td>
          </tr>
          <tr>
              <td align="left">Vendor</td>
              <td><input type="text" name="Vendor" maxlength="50" size="45"></td>
          </tr>
          <tr>
              <td align="left">Qty</td>
              <td><input type="text" name="Qty" maxlength="6" size="6"></td>
          </tr>
          <tr>
              <td align="left">Shipped</td>
              <td>after <input type="text" name="ShippedAfter" maxlength="50" size="13">
                  before <input type="text" name="ShippedBefore" maxlength="50" size="13"></td>
          </tr>
          <tr>
              <td align="left">Received</td>
              <td>after <input type="text" name="ReceivedAfter" maxlength="50" size="13">
                  before <input type="text" name="ReceivedBefore" maxlength="50" size="13"></td>
          </tr>
          <tr>
              <td align="center"><input type="submit" value="Filter"></td>
          </tr>
      </table>
  </form>


  <script>
      $(function () {
        $("#myTable").tablesorter();
      });
      function clearBox(elementID)
      {
          document.getElementById(elementID).innerHTML = "";
      }

      $(document).on("submit", "#filterform", function(event) {
          clearBox("bodyPositive")
          var $form = $(this);

          $.post($form.attr("action"), $form.serialize(), function(response) {
              $.each(response, function(index, product){
                  $("<tr>").appendTo("#bodyPositive")
                      .append($("<td>").text(product.partNumber))
                      .append($("<td>").text(product.partName))
                      .append($("<td>").text(product.vendor))
                      .append($("<td>").text(product.qty))
                      .append($("<td>").text(product.shipped))
                      .append($("<td>").text(product.receive));
              });
              $("#myTable").trigger('update');
          });
         event.preventDefault();
      });

  </script>




  <div id="somediv">
      <table id="myTable" class="tablesorter" border ="1">
          <thead>
          <tr>
              <th>PN</th>
              <th>Part Name</th>
              <th>Vendor</th>
              <th>Qty</th>
              <th>Shipped</th>
              <th>Received</th>
          </tr>
          </thead>
          <tbody id="bodyPositive">
          <%--
          AJAX body
          --%>
          </tbody>
      </table>

  </div>
  </body>
</html>
