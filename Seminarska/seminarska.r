


setwd("C:/Users/Gal/Documents/FRI/UI/Seminarska/")
md <- read.table(file="nbadata.txt", sep=",", header=TRUE)
# anotatiion
# print(md$gmDate)
# print(md$homeAbbr)
# print(md$homeDayOff)
# print(md$homePTS)
# print(md$homeAST)
# print(md$homeTO)
# print(md$homeSTL)
# print(md$homeBLK)
# print(md$homePF)
# print(md$homeFGA)
# print(md$homeFGM)
# print(md$homeDayOff)
# print(md$awayDayOff)
# print(md$awayFGM);

# nekaj uporabnih funkcij
summary(md)
names(md)

h <- vector()

SEASONS = unique(md$gmSeason);
TEAMS = unique(c(md$awayAbbr, md$homeAbbr))

# povprecno stevilo metov trojk obeh ekip skupaj na sezono
for(season in SEASONS) {
    seasonGames <- md$gmSeason == season;
    h <- c(h, mean(md$home3PA[seasonGames] + md$away3PA[seasonGames]));
}
barplot(h, names=SEASONS, main="Povprecno število vseh metov za tri pike v tekmi na sezono");

# stevilo zmag home ekip vs stevilo zmag away ekip
homeWins = table(md$homePTS > md$awayPTS);
pie(homeWins, labels=c("Away", "Home"), main="Zmage doma VS zmage v gostovanju");


# najboljsa ekipa po razmerju zmag
winRatioByTeam = list();
for (team in TEAMS) {
    homeGames <- md$homeAbbr == team;
    awayGames <- md$awayAbbr == team;
    homeWins <- md$homePTS[homeGames] > md$awayPTS[homeGames];
    awayWins <- md$homePTS[awayGames] < md$awayPTS[awayGames];
    totalWins  <- sum(homeWins) + sum(awayWins);
    totalGames <- sum(homeGames) + sum(awayGames);
    ratio <- totalWins / totalGames;
    winRatioByTeam[[team]] <- ratio;
}
winRatioByTeam <- sort(unlist(winRatioByTeam), decreasing=TRUE);
barplot(winRatioByTeam[1:5], names=names(winRatioByTeam)[1:5], main="Top 5 najboljših ekip po razmerju zmag");

# # graf odvisnosti med stevilom prostih dni in razmerju uspesnih metov ekipe

# homeShotSuccessRatio = md$homeFGM / md$homeFGA;
# awayShotSuccessRatio = md$awayFGM / md$awayFGA;
# totalShotSuccessRatio = append(homeShotSuccessRatio, awayShotSuccessRatio);

# daysOff = append(md$homeDayOff, md$awayDayOff);

# plot(totalShotSuccessRatio,daysOff, main="Razmerje uspešnih metov za tri pike domačih ekip odvisno od števila prostih dni", xlab="Število prostih dni", ylab="Razmerje uspešnih metov za tri pike");


# # graf odvisnosti med stevilom prostih dni in razmerju uspesnih prostih metov ekipe

homeFreeShotSuccessRatio = md$homeFTM / md$homeFTA;
awayFreeShotSuccessRatio = md$awayFTM / md$awayFTA;
totalFreeShotSuccessRatio = append(homeShotSuccessRatio, awayShotSuccessRatio);

daysOff = append(md$homeDayOff, md$awayDayOff);

plot(totalFreeShotSuccessRatio,daysOff, main="Razmerje uspešnih metov za tri pike domačih ekip odvisno od števila prostih dni", xlab="Število prostih dni", ylab="Razmerje uspešnih metov za tri pike");




