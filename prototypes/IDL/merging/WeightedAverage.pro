; docformat:'rst'

;+
; A group of procedure and functions to perform weighted averaging.  
;
; :version: 1
;
; :Author:
; 
;   Frederic Melin (European Commission/JRC/IES)
;
; :Copyright:
; 
;   All rights reserved
;   
;-   

;+
; :Author:
;
;   Gert Sclep / Frederic Melin
;
; :version: 1
;   
; :Categories:
; 
;   grid
;   
; :Description:
; 
;   returns number of bins given the number of parallels
;   
;   An example::
;       
;           NbBins = total_nb_bins(res)
;   
; :Pre:
; 
;   The resolution (or number of parallels) must be among a pre-defined set of possible values
;		corresponding to 3rd-, 6th, 12th-, 24th-, or 48th-degree. 
;   
; :Requires:
; 
;   IDL 7.1
;   
; :Params:
;
;   resolution: in, required, type=scalar
;
;-
FUNCTION total_nb_bins, resolution
  CASE resolution OF    
        8640: tot_nb_bins = 95046854
        4320: tot_nb_bins = 23761676
        2160: tot_nb_bins = 5940422
        1080: tot_nb_bins = 1485108
        540: tot_nb_bins = 371272       
  ENDCASE
  RETURN, tot_nb_bins
END

;+
; :Author:
;
;   Frederic Melin
;
; :version: 1
;   
; :Categories:
; 
;   weighted average, merging
;   
; :Description:
; 
;   Performs weighted averaging   
;   
;   An example::
;       
;           WeightedAverage,rrs_1,wts_1,bins_1,rrs_2,wts_2,bins_2,rrs_3,wts_3,bins_3,NBANDS,res,rrs_o,nns_o,bins_o,sensor_flag
;                
;   
; :Pre:
; 
;   The routine calls for 3 sets of values: rrs (data to average), wts (weights) and bins (bin numbers).
;			If one source of data is missing, the corresponding data are set to scalar values (like -1)
;			If valid the values of rrs are (float) arrays of dimensions: [number of bands x number of bins].
;			The number of bands is NBANDS, and it is assumed identical for the 3 sdata sets. 
;			Moreover, the values are assumed all valid for all bands (no case where 1 band of rrs is valid, and another invalid).
;			wts and bins have a dimension of [number of bins].
;			The routine returns the corresponding set rrs_o and bins_o, as well as: 
;			nns_o (going from 1 to 3 depending on the number of sensors included in the average)
;			sensor_flag (byte array, with binary values registering the inclusion of each sensor; e.g.: 2B^0+2B^1+2B^2 indicates the inclusion of data from all 3 sensors).
;			Bad values are returned in case no data are available (but then the routine should not be called).
;			iref: hard-coded index provided the reference for bin number (0 or 1)
;			
;   
; :Requires:
; 
;   IDL 7.1
;   
; :Params:
;
;   rrs_1: in, required, type="fltarr(l,n)" or type=scalar
;	wts_1: in, required, type="fltarr(n)" or type=scalar
;	bins_1: in, required, type="lonarr(n)" or type=scalar
;   rrs_2: in, required, type="fltarr(l,n)" or type=scalar
;	wts_2: in, required, type="fltarr(n)" or type=scalar
;	bins_2: in, required, type="lonarr(n)" or type=scalar
;   rrs_3: in, required, type="fltarr(l,n)" or type=scalar
;	wts_3: in, required, type="fltarr(n)" or type=scalar
;	bins_3: in, required, type="lonarr(n)" or type=scalar
;	NBANDS: in, required, type=integer
;	res: in, required, type=scalar
;	rrs_o: out, required, type="fltarr(l,n)"
;	nns_o: out, required, type="intarr(n)"
;	bins_o: out, required, type="lonarr(n)"
;	sensor_flag: out, required, type="bytarr(n)"
;-
PRO WeightedAverage,rrs_1,wts_1,bins_1,rrs_2,wts_2,bins_2,rrs_3,wts_3,bins_3,NBANDS,res,rrs_o,nns_o,bins_o,sensor_flag

; reference for the bin numbers (0 or 1).
iref = 1
	
; total number of bins for the grid	
NbBins = total_nb_bins(res)

; arrays on global grid
rrs_avg = MAKE_ARRAY(NBANDS,NbBins,/FLOAT,VALUE = 0.) ; sum of rrs, then average after division by weights
wts_avg = MAKE_ARRAY(NbBins,/FLOAT,VALUE = 0.) ; sum of weights
nns_avg = MAKE_ARRAY(NbBins,/INT,VALUE = 0) ; number of data sources for the average
sflag = MAKE_ARRAY(NbBins,/BYTE,VALUE = 0) ; sensor flags

bin_avg = LINDGEN(NbBins)+LONG(iref)

; add data for sensor 1
dd = SIZE(rrs_1)

IF ( dd[0] GT 0 ) THEN BEGIN

FOR il=0,NBANDS-1 DO rrs_avg(il,bins_1(*)-iref) = rrs_avg(il,bins_1(*)-iref)+wts_1(*)*rrs_1(il,*)
wts_avg(bins_1(*)-iref) = wts_avg(bins_1(*)-iref)+wts_1(*)
nns_avg(bins_1(*)-iref) = nns_avg(bins_1(*)-iref) + 1
sflag(bins_1(*)-iref) = sflag(bins_1(*)-iref) + 2B^0

ENDIF

; add data for sensor 2
dd = SIZE(rrs_2)

IF ( dd[0] GT 0 ) THEN BEGIN

FOR il=0,NBANDS-1 DO rrs_avg(il,bins_2(*)-iref) = rrs_avg(il,bins_2(*)-iref)+wts_2(*)*rrs_2(il,*)
wts_avg(bins_2(*)-iref) = wts_avg(bins_2(*)-iref)+wts_2(*)
nns_avg(bins_2(*)-iref) = nns_avg(bins_2(*)-iref) + 1
sflag(bins_2(*)-iref) = sflag(bins_2(*)-iref) + 2B^1

ENDIF

; add data for sensor 3
dd = SIZE(rrs_3)

IF ( dd[0] GT 0 ) THEN BEGIN

FOR il=0,NBANDS-1 DO rrs_avg(il,bins_3(*)-iref) = rrs_avg(il,bins_3(*)-iref)+wts_3(*)*rrs_3(il,*)
wts_avg(bins_3(*)-iref) = wts_avg(bins_3(*)-iref)+wts_3(*)
nns_avg(bins_3(*)-iref) = nns_avg(bins_3(*)-iref) + 1
sflag(bins_3(*)-iref) = sflag(bins_3(*)-iref) + 2B^2

ENDIF

indx = WHERE ( nns_avg GT 0, cnt ) 
; index of bins with valid values; should be at least 1 if at least 1 input had at least 1 valid value
IF ( cnt GT 0 ) THEN BEGIN

FOR il=0,NBANDS-1 DO rrs_avg(il,indx(*)) = rrs_avg(il,indx(*)) / wts_avg(indx(*)) ; compute weighted average

; restrict outputs to valid bins
rrs_o = rrs_avg(*,indx(*))
bins_o = bin_avg(indx)
nns_o = nns_avg(indx)
sensor_flag = sflag(indx)

ENDIF ELSE BEGIN

rrs_o = -9.
bins_o = 0L
nns_o = -1
sensor_flag = 0B

ENDELSE

END
; End of routine