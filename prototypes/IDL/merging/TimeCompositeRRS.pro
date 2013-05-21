
; .comp /home/melinfr/OC/MuSIC/utilities/bin2grid

; .comp /home/melinfr/OC/MuSIC/utilities/get_vds_in_vg.3

; .comp /home/melinfr/OC/MuSIC/utilities/readl3bin_full.4

; .comp /home/melinfr/OC/MuSIC/utilities/create_row_meta_info.2

; .comp /home/melinfr/OC/MuSIC/utilities/writel3bin_full.3


; .run /home/melinfr/OC/MuSIC/TimeCompositeRRS

sensor = 'P'
sensor = 'M'
;sensor = 'A'
;sensor = 'S'
IF ( sensor EQ 'S' ) THEN BEGIN
idir = '/net/netsto1/vol/vol02/data/SeaWiFS/L3/'
odir =  '/net/netsto1/vol/vol02/data/SeaWiFS/L3/MONTHLY/'
iext = 'filtered'
ENDIF

IF ( sensor EQ 'A' ) THEN BEGIN
idir = '/net/netsea1/vol/vol02/data/MODISA/L3/'
odir =  '/net/netsea1/vol/vol02/data/MODISA/L3/MONTHLY/'
idir = '/exports/vol15/data/MODISA/L3/'
odir =  '/exports/vol15/data/MODISA/L3/MONTHLY/'
iext = '9KM.filtered.corrected'
iext = '9KM.b02a'
ENDIF

IF ( sensor EQ 'M' ) THEN BEGIN
idir = '/net/netsea1/vol/vol02/data/MERIS/L3/'
odir =  '/net/netsea1/vol/vol02/data/MERIS/L3/MONTHLY/'
idir = '/exports/vol08/data/MERIS/L3/'
odir =  '/exports/vol08/data/MERIS/MONTHLY/'
iext = '9KM.b02a'
ENDIF

IF ( sensor EQ 'P' ) THEN BEGIN
idir = '/net/netsto1/vol/vol03/data/PML/'
odir =  '/net/netsto1/vol/vol03/data/PML/MONTHLY/'
iext = '9KM.filtered.corrected'
ENDIF

oext = 'L3b_MO_RRS.b02a'

res = 2160
NbBins = total_nb_bins(res)

NBANDS =6 ; +1
badv = -9.

year = [ 2002,2003, 2004, 2005, 2006, 2007 , 2008, 2009 , 2010,2011 ]
;year = [ 2003, 2004, 2005, 2006, 2007]
;year = [ 2002, 2008, 2009 , 2010,2011 ]
NbYears = N_ELEMENTS(year)
NbMonths = 12

sday = ['001','032','060','091','121','152','182','213','244','274','305','335']
sdayb = ['001','032','061','092','122','153','183','214','245','275','306','336']
eday = ['031','059','090','120','151','181','212','243','273','304','334','365']
edayb = ['031','060','091','121','152','182','213','244','274','305','335','366']

smonth = ['01','02','03','04','05','06','07','08','09','10','11','12']

;rrs_m = MAKE_ARRAY(NBANDS,NbBins,/FLOAT,VALUE = 0.)
nns_m = MAKE_ARRAY(NbBins,/INT,VALUE = 0)
bin_m = LINDGEN(NbBins)+1L

; ####################################################################################

isensor = sensor

ifirst = 1

IF ( sensor EQ 'P' ) THEN isensor = 'M'

FOR iy=0,NbYears-1 DO BEGIN
;FOR iy=1,1 DO BEGIN

indir = STRCOMPRESS(idir+STRING(year[iy])+'/',/REMOVE_ALL)

ifile = FINDFILE(indir+isensor+'*'+iext,count = NbDays)
print,year[iy],NbDays

imonth = INTARR(NbDays)

FOR id = 0,NbDays -1 DO BEGIN

   root = STRMID(ifile[id],STRLEN(indir),STRLEN(ifile[id])-STRLEN(indir))
   iday = FIX(STRMID(root,5,3))
   jday = JULDAY(12,31,year[iy]-1)+iday
   CALDAT,jday,month,day
   imonth[id] = FIX(month)
ENDFOR

; Loop over months
FOR im=0,NbMonths-1 DO BEGIN
;FOR im=1,1 DO BEGIN
print,' ------ ',im

root = STRCOMPRESS(isensor+STRING(year[iy])+sday[im]+STRING(year[iy])+eday[im],/REMOVE_ALL)
IF ( year[iy] EQ 2004 OR year[iy] EQ 2008 OR year[iy] EQ 2012 ) THEN $
root = STRCOMPRESS(isensor+STRING(year[iy])+sdayb[im]+STRING(year[iy])+edayb[im],/REMOVE_ALL)

ofile = odir + root + '.'+oext

IF ( ifirst NE 1 ) THEN rrs_m(*,*) = 0.
nns_m(*) = 0

imm = im+1
mindex = WHERE(imonth EQ imm,NbFiles)  

IF ( NbFiles GT 0 ) THEN BEGIN
; --------------------------------------------------------------
; loop over days
FOR id=0,NbFiles-1 DO BEGIN
	
print,ifile[mindex[id]]
readl3bin_full, ifile[mindex[id]], products, rrs, row_meta_info, bin_meta_info, global_attributes

dd = SIZE(rrs)
NBANDS = dd[1]
; normalize by weights for selected bands
FOR il=0,NBANDS-1 DO rrs(il,*) = rrs(il,*)/bin_meta_info.weights(*)

IF ( ifirst EQ 1 ) THEN BEGIN
	rrs_m = MAKE_ARRAY(NBANDS,NbBins,/FLOAT,VALUE = 0.)
	ifirst = -1
ENDIF

FOR il=0,NBANDS-1 DO rrs_m(il,bin_meta_info.bins(*)-1) = rrs_m(il,bin_meta_info.bins(*)-1)+rrs(il,*)
nns_m(bin_meta_info.bins(*)-1) = nns_m(bin_meta_info.bins(*)-1) + 1

ENDFOR ; end of loop over days
; --------------------------------------------------------------
indx = WHERE ( nns_m GT 0,cnt ) 
IF ( cnt GT 0 ) THEN BEGIN

     FOR il=0,NBANDS-1 DO rrs_m(il,indx(*)) = rrs_m(il,indx(*)) / nns_m(indx(*)) ; compute monthly average

ENDIF

rrs = rrs_m(*,indx(*))
bins = bin_m(indx)
nns = nns_m(indx)

bin_info = { bins: bins, $
             nobs: MAKE_ARRAY([cnt],/INTEGER,VALUE=NbFiles),  $
             nscenes: MAKE_ARRAY([cnt],/INTEGER,VALUE=0), $
             weights: MAKE_ARRAY([cnt],/FLOAT,VALUE=1.) }

bin_info.nscenes(*) = nns(*)

create_row_meta_info, res, bin_info.bins, row_meta_info

writel3bin_full, ofile, products, rrs, row_meta_info, bin_info, global_attributes

ENDIF

ENDFOR
; End of loop over months

ENDFOR
; End of loop over years
END

