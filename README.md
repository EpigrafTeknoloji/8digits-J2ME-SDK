# 8digits.com J2ME SDK

## Usage

### Getting client instance
You should create new instance of client in your main midlet.

```java
this.eightDigitsClient = EightDigitsClient.createInstance(this, "<API_URL>", "<TRACKING_URL>");
```

8Digits client is a singleton class, if you want to get instance of class in other midlets, you should type code below in your midlets's startApp event.

```java
this.eightDigitsClient = EightDigitsClient.getInstance();
```

### Creating Auth token (Creating session)
After getting instance of client, you should create auth token to make requests. To create auth token

```java
this.eightDigitsClient.authWithUsername("<USERNAME>", "<PASSWORD>");
```

Creating auth token in your main activity is enough. You don't have to call this method in other activities. You should check return value of this function, if it retursn false, you can't send new event or create a new visit.

### Creating New Visit
After creating your session (Creating auth token), you should call newVisit method which creates sessionCode and hitCode. To track what is happening in your activity, client associates event with hitCode.

```java
this.eightDigitsClient.newVisit("<Title of Visit>", "<Path>");
```

### Creating New Hit
If you want to create new hitCode you can call ```newScreen``` method of client.

```java
this.eightDigitsClient.newScreen("<Screen Name>", "<Screen Path>");
```

### Creating New Event
To create a new event, you can use ```newEvent``` method. You should pass hitCode for creating a new event. Client sends new events to
API asynchronously. So there is no return value.

```java
this.eightDigitsClient.newEvent("<Event Key>", "<Event Value>");
```

### Author

Gurkan Oluc (@grkn) <gurkan@8digits.com>






