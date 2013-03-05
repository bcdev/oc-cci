; NAME:
;   QAA_v5_SEAWIFS_MODIS_MERIS.pro
;
; PURPOSE:
;   Get matrix of QAA IOP from input Rrs for SeawiFS, MODIS and MERIS at 6 wavelengths
;
; CATEGORY:
;   OC-CCI processing
;
; CALLING SEQUENCE:
;   QAA_v5_SEAWIFS_MODIS_MERIS, rrs_in = rrs_in, SENSOR = SENSOR, QAA_MATRIX
;
; INPUTS:
;   rrs_in = rrs at 6 wavelegnths
;                SeaWiFS: [412.,443.,490.,510.,555.,667.]
;                MODIS:   [412.,443.,488.,531.,547.,667.]
;                MERIS:   [413.,443.,490.,510.,560.,665.]
;   SENSOR = SENSOR INDEX (0 = SeaWiFS, 1 = MODIS, 2 = MERIS)
;
; OUTPUTS:
;   QAA matrix of IOPs at 6 wavelengths of each sensor [[a],[aph],[adg],[bb],[bbp]]
;   SeaWiFS: [412.,443.,490.,510.,555.,667.]
;   MODIS:   [412.,443.,488.,531.,547.,667.]
;   MERIS:   [413.,443.,490.,510.,560.,665.]
;
; MODIFICATION HISTORY:
; Bob Brewin 22/02/13 (NEEDS TESTING, DONE VERY QUICKLY, BEWARE IT WAS LAST EDITED AT 8PM ON A FRIDAY!!!!)
;
; EXAMPLE:
;    Rrs_in = [0.00167972470255084,0.00186919071018569,0.0027188008445359,0.00309262196610828,0.00406382197640373,0.00120514585009823] 
;    QAA_v5_SEAWIFS_MODIS_MERIS, rrs_in , SENSOR = 0, QAA_MATRIX
;    print, QAA_MATRIX

   Pro QAA_v5_SEAWIFS_MODIS_MERIS, rrs_in, SENSOR = SENSOR, QAA_MATRIX

   if SENSOR eq 0 then begin
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;SeaWiFS;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   ;;REF wavelength
   REF_wave          = 555.0                                                                 ;SeaWiFS ref 555
   Above_surface_Rrs = Rrs_in
   wavelength        = [412.,443.,490.,510.,555.,667.]
;;;Water coefficients 
   bbw               = [0.00579201,0.00424592,0.00276835,0.00233870,0.00163999,0.000762543]  ;SeaWiFS wave Zhang et al. (2009) S=35; T=22C
   aw                = [0.00455056,0.00706914,0.0150000,0.0325000,0.0596000,0.434888]        ;SeaWiFS wave Pope & fry 199
   endif
   
   if SENSOR eq 1 then begin
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;MODIS;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;INPUT
   ;;REF wavelength
   REF_wave          = 547.0                                                                 ;MODIS ref 547
   Above_surface_Rrs = Rrs_in
   wavelength        = [412.,443.,488.,531.,547.,667.]
;;;Water coefficients 
   bbw               = [0.00579201,0.00424592,0.00281659,0.00197385,0.00174280,0.000762543]  ;MODIS wave Zhang et al. (2009) S=35; T=22C
   aw                = [0.00455056,0.00706914,0.0145167,0.0439153,0.0531686,0.434888]        ;MODIS wave Pope & fry 1997   
   endif
   
   if SENSOR eq 2 then begin
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;MERIS;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;INPUT
   ;;REF wavelength
   REF_wave          = 560.0                                                                 ;MERIS ref 560
   Above_surface_Rrs = Rrs_in
   wavelength        = [413.,443.,490.,510.,560.,665.]
;;;Water coefficients 
   bbw               = [0.00573196,0.00424592,0.00276835,0.00233870,0.00157958,0.000772104]  ;MERIS  wave Zhang et al. (2009) S=35; T=22C
   aw                = [0.00449607,0.00706914,0.0150000,0.0325000,0.0619000,0.429000]        ;MERIS  wave Pope & fry 1997   
   endif
   
;;;Check Rrs(667 or 665)
   UP_667 = 20.*(Above_surface_Rrs(4))^1.5
   LW_667 = 0.9*(Above_surface_Rrs(4))^1.7
   if Above_surface_Rrs(5) gt UP_667 then Above_surface_Rrs(5) = 1.27*(Above_surface_Rrs(4))^1.47+0.00018*(Above_surface_Rrs(2)/Above_surface_Rrs(4))^(-3.19)
   if Above_surface_Rrs(5) lt LW_667 then Above_surface_Rrs(5) = 1.27*(Above_surface_Rrs(4))^1.47+0.00018*(Above_surface_Rrs(2)/Above_surface_Rrs(4))^(-3.19)      
;;;Coefficients for converting Rrs to rrs (above to below sea-surface)   
   coff1 = 0.52
   coff2 = 1.7
   Below_surface_Rrs = Above_surface_Rrs / (coff1 + coff2*Above_surface_Rrs)
;;;Coefficients as defined by Gordon et al. (1988) and modified by Lee et al. (2002) to estimate bb/a+bb refered to as U
   g0 = 0.089
   g1 = 0.125    
   U  = (-g0 + ((g0^2.) + 4. * g1 * Below_surface_Rrs)^0.5) / (2. * g1)
;;;Estimation of a at reference wavelength 
   X     = ALOG10((Below_surface_Rrs(1)+Below_surface_Rrs(2))/(Below_surface_Rrs(4)+5.*(Below_surface_Rrs(5)/Below_surface_Rrs(2)) * Below_surface_Rrs(5)))
   a_555 = aw(4) + 10^(-1.146+(-1.366*X)+(-0.469*X^2))
;;;Estimation of bbp at reference wavelength 
   bbp_555 = (U(4)*a_555/(1-U(4)))-bbw(4)
;;;Exponent of bbp
   N     = 2.*(1.-1.2*EXP(-0.9*Below_surface_Rrs(1)/Below_surface_Rrs(4)))
;;;Estimate ratio of aph411/aph443
   Ratio_aph = 0.74+(0.2/(0.8 + Below_surface_Rrs(1)/Below_surface_Rrs(4)))
;;;Estimate ratio of adg411/adg443
   Slope_adg = 0.015+(0.002/(0.6 + Below_surface_Rrs(1)/Below_surface_Rrs(4)))
   Ratio_adg = exp(Slope_adg*(wavelength(1)-wavelength(0)))
;;;Estimation of bbp  and bb at all wavelengths
   bbp = bbp_555*(REF_wave/wavelength)^N
   bb  = bbp + bbw
;;;Estimation of a at all wavelengths
   a = (1.-U)*(bbp+bbw)/U
;;;Estimation of adg at all wavelengths
   adg_443 =((a(0)-Ratio_aph*a(1))-(aw(0)-Ratio_aph*aw(1)))/(Ratio_adg-Ratio_aph)
   adg     = adg_443*exp(-Slope_adg*(wavelength-wavelength(1)))
;;;Estimation of aph at all wavelengths
   aph = a-aw-adg
   QAA_MATRIX = [[a],[aph],[adg],[bb],[bbp]]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;SeaWiFS;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
end