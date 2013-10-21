function [betaw,beta90w,bw]=betaw_ZH2009(lambda,T,theta,delta)
% computes pure water scattering functions:
% Zhang & Hu, 2009, Optics Express, 17(3), 1671-1678
% lambda in nm
% T in degC
% theta in degree
% delta: depolarization ratio, if not provided, default = 0.039 will be
% used.
% betasw: volume scattering at angles defined by theta. Its size is [x y],
% where x is the number of angles (x = length(theta)) and y is the number
% of wavelengths in lambda (y = length(lambda))
% beta90w: volume scattering at 90 degree. Its size is [1 y]
% bw: total scattering coefficient. Its size is [1 y]
% for backscattering coefficients, divide total scattering by 2
%

error(nargchk(3, 4, nargin));
if nargin == 3
    delta = 0.039; % Farinato and Roswell (1976)
end

lambda = lambda(:)'; % make lambda a row vector
theta = theta(:); % make theta a column vector

rad=theta/180*pi;
k=1.38054e-23; %1.38054e-23 Boltzmann constant
n_wat=1.31405-2.02e-6*T^2+(15.868-0.00423*T)*lambda.^-1-4382*lambda.^-2+1.1455e6*lambda.^-3; %from Quan & Fry
% n_air=1.0 + 0.0002926/(1+0.0036805*T);
n_air=1+(5792105./(238.0185-(lambda/1000).^-2)+ 167917./(57.362-(lambda/1000).^-2))*1e-8;  %Ciddor 1996
n_wat=n_wat.*n_air;
isothermal_compress=(50.88630+0.7171582*T+0.781986e-3*T^2+31.62214e-6*T^3 ...
    -0.1323594e-6*T^4+0.6345750e-9*T^5)/(1.0+21.65928e-3*T)*1e-11; % from Kell

% choose one of the following, recommend PMH
n_density_derivative= PMH(n_wat);
% n_density_derivative= Niedrich(n_wat);
% n_density_derivative= Eisenberg(n_wat,lambda);
% n_density_derivative= LL(n_wat);
% n_density_derivative= laplace(n_wat);


%***PURE WATER
%***Einstein-Smoluchowski Eqn
beta90w=0.5*pi^2*k*(273+T)*((lambda*1e-9).^-4)*isothermal_compress.*...
n_density_derivative.^2*(6+6*delta)/(6-7*delta);
bw=8*pi/3*beta90w*(2+delta)/(1+delta);
for i=1:length(lambda)
    betaw(:,i)=beta90w(i)*(1+((cos(rad)).^2).*(1-delta)/(1+delta));
end

% density derivative of refractive index from PMH model
function n_density_derivative=PMH(n_wat)
n_wat2 = n_wat.^2;
n_density_derivative=(n_wat2-1).*(1+2/3*(n_wat2+2).*(n_wat/3-1/3./n_wat).^2);

% density derivative of refractive index from Niedrich model
function n_density_derivative=Niedrich(n_wat)
n_wat2 = n_wat.^2;
n_density_derivative=(n_wat2-1).*(2*n_wat2+1)./(2*n_wat2+1./n_wat2);

% density derivative of refractive index from Eisenberg model
function n_density_derivative=Eisenberg(n_wat,lambda)
wv = [404.66 435.83 447.15 471.31 486.13 501.57 546.07 576.96 ...
    587.56 589.26 656.28 667.81 706.52];
B = [0.87642 0.87898 0.87974 0.88110 0.88180 0.88249 0.88410 ...
    0.88503 0.88532 0.88538 0.88694 0.88714 0.88796];
B1 = interp1(wv,B,lambda,'cubic');
n_wat2 = n_wat.^2;
n_density_derivative=B1/3.*(n_wat2-1).*(n_wat2+2);

% density derivative of refractive index from Laplace model
function n_density_derivative=laplace(n_wat)
n_wat2 = n_wat.^2;
n_density_derivative=n_wat2-1;

% density derivative of refractive index from Lorentz-Lorenz model
function n_density_derivative=LL(n_wat)
n_wat2 = n_wat.^2;
n_density_derivative=(n_wat2-1).*(n_wat2+2)/3;