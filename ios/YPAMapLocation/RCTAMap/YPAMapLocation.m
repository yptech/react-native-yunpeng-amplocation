//
//  RCTAMapLocation.h
//  RCTAMap
//
//  Created by yiyang on 16/4/11.
//  Copyright © 2016年 creditease. All rights reserved.
//


#import <AMapLocationKit/AMapLocationKit.h>

#import "YPAMapLocation.h"
#import "RCTBridge.h"
#import "RCTConvert.h"
#import "RCTEventDispatcher.h"

typedef NS_ENUM(NSInteger, KKRCTPositionErrorCode) {
    KKRCTPositionErrorDenied = 1,
    KKRCTPositionErrorUnavailable,
    KKRCTPositionErrorTimeout,
};

typedef struct {
    double timeout;
    double maximumAge;
    double accuracy;
    double distanceFilter;
} KKLocationOptions;

#define KK_DEFAULT_LOCATION_ACCURACY kCLLocationAccuracyHundredMeters

@implementation RCTConvert (KKLocationOptions)

+ (KKLocationOptions)KKLocationOptions:(id)json
{
    NSDictionary<NSString *, id> *options = [RCTConvert NSDictionary:json];
    
    double distanceFilter = options[@"distanceFilter"] == NULL ? KK_DEFAULT_LOCATION_ACCURACY
    : [RCTConvert double:options[@"distanceFilter"]] ?: kCLDistanceFilterNone;
    
    return (KKLocationOptions){
        .timeout = [RCTConvert NSTimeInterval:options[@"timeout"]] ?: INFINITY,
        .maximumAge = [RCTConvert NSTimeInterval:options[@"maximumAge"]] ?: INFINITY,
        .accuracy = [RCTConvert BOOL:options[@"enableHighAccuracy"]] ? kCLLocationAccuracyBest : KK_DEFAULT_LOCATION_ACCURACY,
        .distanceFilter = distanceFilter
    };
}

@end

static NSDictionary<NSString *, id> *KKRCTPositionError(KKRCTPositionErrorCode code, NSString *msg /* nil for default */)
{
    if (!msg) {
        switch (code) {
            case KKRCTPositionErrorDenied:
                msg = @"User denied access to location services.";
                break;
            case KKRCTPositionErrorUnavailable:
                msg = @"Unable to retrieve location.";
                break;
            case KKRCTPositionErrorTimeout:
                msg = @"The location request timed out.";
                break;
        }
    }
    
    return @{
             @"code": @(code),
             @"message": msg,
             @"PERMISSION_DENIED": @(KKRCTPositionErrorDenied),
             @"POSITION_UNAVAILABLE": @(KKRCTPositionErrorUnavailable),
             @"TIMEOUT": @(KKRCTPositionErrorTimeout)
             };
}

@interface KKLocationRequest : NSObject

@property (nonatomic, copy) RCTPromiseResolveBlock resolve;
@property (nonatomic, copy) RCTPromiseRejectBlock reject;
@property (nonatomic, assign) KKLocationOptions options;
@property (nonatomic, strong) NSTimer *timeoutTimer;

@end

@implementation KKLocationRequest

- (void)dealloc
{
    if (_timeoutTimer.valid) {
        [_timeoutTimer invalidate];
    }
}

@end

@interface YPAMapLocation () <AMapLocationManagerDelegate>

@end

@implementation YPAMapLocation
{
    AMapLocationManager *_locationManager;
    NSDictionary<NSString *, id> *_lastLocationEvent;
    BOOL _observingLocation;
    NSMutableArray<KKLocationRequest *> *_pendingRequests;
    KKLocationOptions _observerOptions;
}

RCT_EXPORT_MODULE(YPAMapLocation);

@synthesize bridge = _bridge;

- (void)dealloc
{
    [_locationManager stopUpdatingLocation];
    _locationManager.delegate = nil;
}

-(dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

#pragma mark - Private API

- (void)beginLocationUpdatesWithDesiredAccuracy:(CLLocationAccuracy)desiredAccuracy
{
    
    if (!_locationManager) {
        _locationManager = [AMapLocationManager new];
        _locationManager.distanceFilter = _observerOptions.distanceFilter;
        _locationManager.delegate = self;
    }
    
    _locationManager.desiredAccuracy = desiredAccuracy;
    [_locationManager startUpdatingLocation];
}

#pragma mark - Timeout handler

- (void)timeout:(NSTimer *)timer
{
    KKLocationRequest *request = timer.userInfo;
    NSString *message = [NSString stringWithFormat: @"Unable to fetch location within %zds.", (NSInteger)(timer.timeInterval * 1000.0)];
    request.reject([NSString stringWithFormat:@"%ld", KKRCTPositionErrorTimeout], message, [NSError errorWithDomain:message code:KKRCTPositionErrorTimeout userInfo:nil]);
    [_pendingRequests removeObject:request];
    
    // Stop updating if no pending requests
    if (_pendingRequests.count == 0 && !_observingLocation) {
        [_locationManager stopUpdatingLocation];
    }
}

#pragma mark - Public API

RCT_REMAP_METHOD(startObserving, startObserving:(KKLocationOptions)options resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [self checkLocationConfig];
    
    // Select best options
    _observerOptions = options;
    for (KKLocationRequest *request in _pendingRequests) {
        _observerOptions.accuracy = MIN(_observerOptions.accuracy, request.options.accuracy);
    }
    
    [_locationManager setAllowsBackgroundLocationUpdates:YES];
    [_locationManager setPausesLocationUpdatesAutomatically:NO];
    [self beginLocationUpdatesWithDesiredAccuracy:_observerOptions.accuracy];
    _observingLocation = YES;
    resolve(nil);
}

RCT_REMAP_METHOD(stopObserving, resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    // Stop observing
    _observingLocation = NO;
    
    // Stop updating if no pending requests
    if (_pendingRequests.count == 0) {
        [_locationManager stopUpdatingLocation];
    	resolve(nil);
    }
}

RCT_REMAP_METHOD(getCurrentPosition, getCurrentPosition:(KKLocationOptions)options
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    [self checkLocationConfig];
    if (![CLLocationManager locationServicesEnabled]) {
    	reject([NSString stringWithFormat:@"%ld", KKRCTPositionErrorUnavailable], @"Location services disabled.", [NSError errorWithDomain:@"Location services disabled." code:KKRCTPositionErrorUnavailable userInfo:nil]);
     	return;
    }
    
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied) {
        reject([NSString stringWithFormat:@"%ld", KKRCTPositionErrorDenied], @"Location services denied.", [NSError errorWithDomain:@"Location services denied." code:KKRCTPositionErrorDenied userInfo:nil]);
        return;
    }
    
    // Check if previous recorded location exists and is good enough
    if (_lastLocationEvent &&
        [NSDate date].timeIntervalSince1970 - [RCTConvert NSTimeInterval:_lastLocationEvent[@"timestamp"]] < options.maximumAge &&
        [_lastLocationEvent[@"coords"][@"accuracy"] doubleValue] <= options.accuracy) {
        
        // Call success block with most recent known location
        resolve(_lastLocationEvent);
        return;
    }
    
    // Create request
    KKLocationRequest *request = [KKLocationRequest new];
    request.resolve = resolve;
    request.reject = reject;
    request.options = options;
    request.timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:options.timeout
                                                            target:self
                                                          selector:@selector(timeout:)
                                                          userInfo:request
                                                           repeats:NO];
    if (!_pendingRequests) {
        _pendingRequests = [NSMutableArray new];
    }
    [_pendingRequests addObject:request];
    
    // Configure location manager and begin updating location
    CLLocationAccuracy accuracy = options.accuracy;
    if (_locationManager) {
        accuracy = MIN(_locationManager.desiredAccuracy, accuracy);
    }
    [self beginLocationUpdatesWithDesiredAccuracy:accuracy];
}

#pragma mark - AMapLocationManagerDelegate

- (void)amapLocationManager:(AMapLocationManager *)manager didUpdateLocation:(CLLocation *)location
{
    if (location == nil) {
        return;
    }
    NSLog(@"[AMap]didUpdateLocation: %@", location);
    _lastLocationEvent = @{
                           @"coords": @{
                                   @"latitude": @(location.coordinate.latitude),
                                   @"longitude": @(location.coordinate.longitude),
                                   @"altitude": @(location.altitude),
                                   @"accuracy": @(location.horizontalAccuracy),
                                   @"altitudeAccuracy": @(location.verticalAccuracy),
                                   @"heading": @(location.course),
                                   @"speed": @(location.speed),
                                   },
                           @"timestamp": @([location.timestamp timeIntervalSince1970] * 1000) // in ms
                           };
    
    // Send event
    if (_observingLocation) {
        [_bridge.eventDispatcher sendDeviceEventWithName:@"yyAMapLocationDidChange"
                                                    body:_lastLocationEvent];
    }
    
    // Fire all queued callbacks
    for (KKLocationRequest *request in _pendingRequests) {
        request.resolve(_lastLocationEvent);
        [request.timeoutTimer invalidate];
    }
    [_pendingRequests removeAllObjects];
    
    // Stop updating if not observing
    if (!_observingLocation) {
        [_locationManager stopUpdatingLocation];
    }
    
    // Reset location accuracy if desiredAccuracy is changed.
    // Otherwise update accuracy will force triggering didUpdateLocations, watchPosition would keeping receiving location updates, even there's no location changes.
    if (ABS(_locationManager.desiredAccuracy - KK_DEFAULT_LOCATION_ACCURACY) > 0.000001) {
        _locationManager.desiredAccuracy = KK_DEFAULT_LOCATION_ACCURACY;
    }

}

- (void)amapLocationManager:(AMapLocationManager *)manager didFailWithError:(NSError *)error
{
    // Check error type
    NSDictionary<NSString *, id> *jsError = nil;
    switch (error.code) {
        case kCLErrorDenied:
            jsError = KKRCTPositionError(KKRCTPositionErrorDenied, nil);
            break;
        case kCLErrorNetwork:
            jsError = KKRCTPositionError(KKRCTPositionErrorUnavailable, @"Unable to retrieve location due to a network failure");
            break;
        case kCLErrorLocationUnknown:
        default:
            jsError = KKRCTPositionError(KKRCTPositionErrorUnavailable, nil);
            break;
    }
    
    // Send event
    if (_observingLocation) {
        [_bridge.eventDispatcher sendDeviceEventWithName:@"yyAMapLocationError"
                                                    body:jsError];
    }
    
    // Fire all queued error callbacks
    for (KKLocationRequest *request in _pendingRequests) {
        request.reject([NSString stringWithFormat:@"%ld", error.code], error.domain, error);
        [request.timeoutTimer invalidate];
    }
    [_pendingRequests removeAllObjects];
    
    // Reset location accuracy if desiredAccuracy is changed.
    // Otherwise update accuracy will force triggering didUpdateLocations, watchPosition would keeping receiving location updates, even there's no location changes.
    if (ABS(_locationManager.desiredAccuracy - KK_DEFAULT_LOCATION_ACCURACY) > 0.000001) {
        _locationManager.desiredAccuracy = KK_DEFAULT_LOCATION_ACCURACY;
    }

}

#pragma mark - Helpers

- (void)checkLocationConfig
{
    if (!([[NSBundle mainBundle] objectForInfoDictionaryKey:@"NSLocationWhenInUseUsageDescription"] ||
          [[NSBundle mainBundle] objectForInfoDictionaryKey:@"NSLocationAlwaysUsageDescription"])) {
        RCTLogError(@"Either NSLocationWhenInUseUsageDescription or NSLocationAlwaysUsageDescription key must be present in Info.plist to use geolocation.");
    }
}

@end
