//
//  ServiceClient.h
//  FANPOWER
//
//  Created by Fábio Bernardo on 6/14/13.
//  Copyright (c) 2013 yoyo AG. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface ServiceClient : NSObject

+ (ServiceClient *)sharedServiceClient;

-(void)fetchAddressesWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock;
-(void)fetchScheduleDataWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock;
-(void)fetchOrdersWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock;
-(void)fetchSetRatingWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock withRating:(id)rating withView:(UIView*)view;
-(void)fetchOrderForIdWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock withOrderId:(NSString*)orderId;
-(void)fetchCancelOrderForIdWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock withOrderId:(NSString*)orderId withView:(UIView*)view;
-(void)fetchStripeCardsForIdWithCompletionBlock:(void (^)(id))completionBlock failureBlock:(void (^)(NSError *error))failureBlock withView:(UIView*)view;

@end
