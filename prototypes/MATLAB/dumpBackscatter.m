file_id = fopen('/home/tom/Dev/Projects/oc-cci/prototypes/MATLAB/bbw.txt', 'w');
fprintf(file_id, "{");

wl = 410.0;
while wl < 693
    [betasw,beta90sw,bsw] = betasw_ZHH2009(wl, 22, 90, 35);
    fprintf(file_id, "%.7f,", bsw);
    wl = wl + 2.5;
endwhile

fprintf(file_id, "}");

fclose(file_id);