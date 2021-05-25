
const toggleSidebar = ()=>{

  if($(".sidebar").is(":visible"))
  {
  		$(".sidebar").css("display","none");
  		$(".content").css("margin-left","0%");
  }
  else{
  		$(".sidebar").css("display","block");
  		$(".content").css("margin-left","20%");
  }
}
const search=()=>{
  let q=$("#search-input").val();
 
  if(q=="")
  {
  	$(".search-result").hide();
  }
  else{

  let url=`http://localhost:1200/search/${q}`;

  fetch(url).
  then((response)=>{
  	return response.json(); 
  })
  .then((data)=>{
 	
 	let text=`<div class="list-group">`
 	
 	data.forEach((contact)=>{
 	text+=`<a href="/user/contact/${contact.cid}" class="list-group-item list-group-item-action">${contact.name}</a>`
 	});	
 	text+=`</div>`;
 	$(".search-result").html(text);
 	$(".search-result").show();
 });
   $(".search-result").show();
  }
};
