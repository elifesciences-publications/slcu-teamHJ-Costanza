%global image
%Initialise a snake by hand
function [xs, ys] = getsnake(image);
%GETSNAKE   Read snake control points
%   [XS, YS] = GETSNAKE(IMAGE) displays the image in a new figure, the
%   records the position of each click of button 1 of the mouse in the
%   figure, and stops after recording the position of one click of
%   another button. The snake is drawn as it goes along. XS is a column
%   vector of x-coordinates and YS a column vector of y-coordinates.

figure;
%imshow(image);     % display image
colormap(gray);     %ändrat från imshow till imagesc för att kunna visa randig bild
axis('square', 'on');
imagesc(image);
hold on;           % and keep it there while we plot
xs = [];
ys = [];           % initialisations
xold = 0;
yold = 0;
but = 1;

while but == 1                      % while button 1 being pressed
    [xi, yi, but] = ginput(1);      % get a point
    xs = [xs; xi];                  % append its coords to vector
    ys = [ys; yi];
    if xold;
        plot([xold xi], [yold yi], 'go-');  % draw as we go
    else
        plot(xi, yi, 'go');         % first point on its own
    end;
    xold = xi;
    yold = yi;
end;

plot([xi xs(1)], [yi ys(1)], 'g-'); % join first to last points
hold off;

