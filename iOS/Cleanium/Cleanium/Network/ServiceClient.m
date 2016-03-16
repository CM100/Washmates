//
//  ServiceClient.m
//  FANPOWER
//
//  Created by Fábio Bernardo on 6/14/13.
//  Copyright (c) 2013 yoyo AG. All rights reserved.
//

#import "ServiceClient.h"
#import "GetAddressesFetcher.h"
#import "GetScheduleData.h"
#import "GetOrdersFetcher.h"
#import "SetRatingFetcher.h"
#import "GetOrderForId.h"
#import "CancelOrderFetcher.h"
#import "GetStripeCards.h"

#define kServiceClientUserTimeout (5 * 60)

@implementation ServiceClient {
   
}


#pragma mark - Static Methods

+ (ServiceClient *)sharedServiceClient {
    static dispatch_once_t once;
    static ServiceClient *serviceClient;
    dispatch_once(&once, ^{
        serviceClient = [[ServiceClient alloc] init];
    });
    return serviceClient;
}

#pragma mark - Fetching

-(void)fetchAddressesWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock{
    assert(completionBlock != nil);
    
    GetAddressesFetcher *fetcher = [[GetAddressesFetcher alloc] init];
    [fetcher fetch:^(id resultObject, NSError *error) {
        if (error) {
            if (failureBlock) failureBlock(error);
            return;
        }
        
        if (resultObject) {
            completionBlock(resultObject);
        } else {
            completionBlock(nil);
        }
    }];
}

-(void)fetchScheduleDataWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock{
    assert(completionBlock != nil);
    
    GetScheduleData *fetcher = [[GetScheduleData alloc] init];
    [fetcher fetch:^(id resultObject, NSError *error) {
        if (error) {
            if (failureBlock) failureBlock(error);
            return;
        }
        
        if (resultObject) {
            completionBlock(resultObject);
        } else {
            completionBlock(nil);
        }
    }];
}

-(void)fetchOrdersWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock{
    assert(completionBlock != nil);
    
    GetOrdersFetcher *fetcher = [[GetOrdersFetcher alloc] init];
    [fetcher fetch:^(id resultObject, NSError *error) {
        if (error) {
            if (failureBlock) failureBlock(error);
            return;
        }
        
        if (resultObject) {
            completionBlock(resultObject);
        } else {
            completionBlock(nil);
        }
    }];
}

-(void)fetchSetRatingWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock withRating:(id)rating withView:(UIView*)view{
    assert(completionBlock != nil);
    
    SetRatingFetcher *fetcher = [[SetRatingFetcher alloc] init];
    fetcher.rating = rating;
    fetcher.view = view;
    [fetcher fetch:^(id resultObject, NSError *error) {
        if (error) {
            if (failureBlock) failureBlock(error);
            return;
        }
        
        if (resultObject) {
            completionBlock(resultObject);
        } else {
            completionBlock(nil);
        }
    }];
}

-(void)fetchOrderForIdWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock withOrderId:(NSString*)orderId{
    assert(completionBlock != nil);
    
    GetOrderForId *fetcher = [[GetOrderForId alloc] init];
    fetcher.orderId = orderId;
    [fetcher fetch:^(id resultObject, NSError *error) {
        if (error) {
            if (failureBlock) failureBlock(error);
            return;
        }
        
        if (resultObject) {
            completionBlock(resultObject);
        } else {
            completionBlock(nil);
        }
    }];
}

-(void)fetchCancelOrderForIdWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock withOrderId:(NSString*)orderId withView:(UIView*)view{
    assert(completionBlock != nil);
    
    CancelOrderFetcher *fetcher = [[CancelOrderFetcher alloc] init];
    fetcher.orderId = orderId;
    fetcher.view = view;
    [fetcher fetch:^(id resultObject, NSError *error) {
        if (error) {
            if (failureBlock) failureBlock(error);
            return;
        }
        
        if (resultObject) {
            completionBlock(resultObject);
        } else {
            completionBlock(nil);
        }
    }];
}

-(void)fetchStripeCardsForIdWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock withView:(UIView*)view{
    assert(completionBlock != nil);
    
    GetStripeCards *fetcher = [[GetStripeCards alloc] init];
    fetcher.view = view;
    [fetcher fetch:^(id resultObject, NSError *error) {
        if (error) {
            if (failureBlock) failureBlock(error);
            return;
        }
        
        if (resultObject) {
            completionBlock(resultObject);
        } else {
            completionBlock(nil);
        }
    }];
}

@end
