//
//  CalculatedDate.h
//  Cleanium
//
//  Created by  DN-117 on 7/31/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CalculatedDate : NSObject

@property(nonatomic, retain) NSString *calcMinYear;
@property(nonatomic, retain) NSString *calcMaxYear;
@property(nonatomic, retain) NSMutableDictionary *calcDateDictionary;

@end
