/*
 * Pathlopedia.JS
 * 
 * @author Ugur Kocak
 * 
 */

window.onload=function(){
    pp.initializeMap();
};

var pp={
    map:null,
    
    madDivID:"ppmap",
    
    browserLocationSupport:false,
    
    initCoords:{
        lat: 41.023362521843865,
        lng: 28.9874267578125
    },
    
    initializeMap:function(){
        
        var mapOptions={
            center: new google.maps.LatLng(this.initCoords.lat, this.initCoords.lng),
            mapTypeId: google.maps.MapTypeId.HYBRID,
            disableDefaultUI: true,
            zoom: 15,
            zoomControl: true
        };

        this.map = new google.maps.Map(document.getElementById(this.madDivID), mapOptions);
        
        this.setGeoLocation();  
    },
    
    setGeoLocation:function(){
        // Try W3C Geolocation
        if(navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                this.initCoords.lat=position.coords.latitude;
                this.initCoords.lng=position.coords.longitude;
                this.map.setCenter(new google.maps.LatLng(position.latitude,position.longitude));
            });
        }else {
                alert("Your browser doesn't support geolocation.");
        }
    }
}
