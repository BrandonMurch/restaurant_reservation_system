#Restaurant Reservation System

#####A restful web service that allows a front end website to make restaurant reservations, store them in a database, then retrieve them later.

####Format Examples

    Get query = /bookings?startTime='2011-12-03T10:15:30'&endTime='2011-12-03T10:15:31'
    
    Date Time Format = '2011-12-03T10:15:30'
    
    Date Format = '2011-12-03'
    
    Time Format = '10:15:30'
    
####Exceptions

    When the request causes exceptions, it will be returned in the following JSON format:
    {
        status: "HttpStatusCode ex. CONFLICT or BAD_REQUEST", 
        timestamp: "2020-07-22@12:59:03",
        message: "message", 
        subErrors: []
    }

####/bookings
    (Get) Retrieve all bookings, or those based on queries:
        Queried by: (see format above)
            Start time / end time (to find all bookings within this range)
            Start Time
            Date
        Returns a list of bookings.
    
    (Post) Create a new booking:
        Request body that contains both user and booking.
        Returns a response entity depending on the result.
        
    (Put) Update an existing booking:
        Request body that contains a booking.
        Returns response entity depending on the result.
        
    
####/bookings/{id}
    (Get) Get a booking by its id: 
        Returns a single booking
     
     (Delete) Delete a single booking:
        Returns response entity depending on the result.
        
####/restaurant/availability
    (Get) Get all available dates or times to make a reservation:
        Queried by: (see format above)
            Date / size:
                Returns all times on the specified day, for the specified party size
            None:
                Returns all available Dates
        
####/users
    (Get) Retrieve all users, or those based on queries:
        Queried by: (see format above)
            Email
        Returns a list of users.
    
    (Post) Create a new user:
        Request body that contains a User.
        Returns a response entity depending on the result.
    
####/bookings/{id}
    (Get) Get a user by its id: 
        Returns a single user
    
    (Put) Update an existing user:
            Request body that contains a user.
            Returns response entity depending on the result.
     
     (Delete) Delete a single user:
        Returns response entity depending on the result.
        
Copyright 2020 Brandon Murch