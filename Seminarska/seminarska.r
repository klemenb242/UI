

library(CORElearn)

# HELPERS

Sensitivity <- function(obs, pred, pos.class)
{
    TP <- sum(obs == pos.class & pred == pos.class)
    FN <- sum(obs == pos.class & pred != pos.class)
    return(TP / (TP + FN))
}

Specificity <- function(obs, pred, pos.class)
{
    TN <- sum(obs != pos.class & pred != pos.class)
    FP <- sum(obs != pos.class & pred == pos.class)
    return(TN / (TN + FP))
}

Precision <- function(obs, pred, pos.class)
{
    TP <- sum(obs == pos.class & pred == pos.class)
    FP <- sum(obs != pos.class & pred == pos.class)
    return(TP / (TP + FP))
}

Split70to30 <- function(data)
{
    train <- data[1:round(0.7*nrow(data)),]
    test <- data[-(1:round(0.7*nrow(data))),]
    return(list(train, test))
}

attr <- function(position, attrName) {
    return(paste(position, attrName, sep=""));
}






# __START

set.seed(0)

setwd("C:/Users/Gal/Documents/FRI/UI/Seminarska/")
md <- read.table(file="nbadata.txt", sep=",", header=TRUE)

md <- md[order(md$gmDate),];


SEASONS = unique(md$gmSeason);
TEAMS = unique(c(md$awayAbbr, md$homeAbbr))

# # VIZUALIZACIJA
# # povprecno stevilo metov trojk obeh ekip skupaj na sezono
# averageThrees <- vector()
# for(season in SEASONS) {
#     seasonGames <- md$gmSeason == season;
#     averageThrees <- c(averageThrees, mean(md$home3PA[seasonGames] + md$away3PA[seasonGames]));
# }
# barplot(averageThrees, names=SEASONS, main="Povprecno število vseh metov za tri pike v tekmi na sezono");

# # stevilo zmag home ekip vs stevilo zmag away ekip
# homeWins = table(md$homePTS > md$awayPTS);
# pie(homeWins, labels=c("Away", "Home"), main="Zmage doma VS zmage v gostovanju", labels = round(values/sum(values)*100, 2)));


# # najboljsa ekipa po razmerju zmag
# winRatioByTeam = list();
# for (team in TEAMS) {
#     homeGames <- md$homeAbbr == team;
#     awayGames <- md$awayAbbr == team;
#     homeWins <- md$homePTS[homeGames] > md$awayPTS[homeGames];
#     awayWins <- md$homePTS[awayGames] < md$awayPTS[awayGames];
#     totalWins  <- sum(homeWins) + sum(awayWins);
#     totalGames <- sum(homeGames) + sum(awayGames);
#     ratio <- totalWins / totalGames;
#     winRatioByTeam[[team]] <- ratio;
# }
# winRatioByTeam <- sort(unlist(winRatioByTeam), decreasing=TRUE);
# barplot(winRatioByTeam[1:5], names=names(winRatioByTeam)[1:5], main="Top 5 najboljših ekip po razmerju zmag");


# # Graf odvisnosti uspesnih prostih metov od stevila prostih dni
# homeFreeShotSuccessRatio = md$homeFTM / md$homeFTA;
# awayFreeShotSuccessRatio = md$awayFTM / md$awayFTA;
# totalFreeShotSuccessRatio = append(homeFreeShotSuccessRatio, awayFreeShotSuccessRatio);
# daysOff = append(md$homeDayOff, md$awayDayOff);
# plot(totalFreeShotSuccessRatio,daysOff, main="Razmerje uspešnih prostih metov odvisno od števila prostih dni", xlab="Število prostih dni", ylab="Razmerje uspešnih metov za tri pike");





structureTeamData <- function(games, position)
{
    teamStatistics <- list();    
    teamStatistics[[attr(position, "PTS")]] <- mean(games[[attr(position, "PTS")]]);
    teamStatistics[[attr(position, "AST")]] <- mean(games[[attr(position, "AST")]]);
    teamStatistics[[attr(position, "TO")]] <- mean(games[[attr(position, "TO")]]);
    teamStatistics[[attr(position, "STL")]] <- mean(games[[attr(position, "STL")]]);
    teamStatistics[[attr(position, "BLK")]] <- mean(games[[attr(position, "BLK")]]);
    teamStatistics[[attr(position, "PF")]] <- mean(games[[attr(position, "PF")]]);

    teamStatistics[[attr(position, "ORB")]] <- mean(games[[attr(position, "ORB")]]);
    teamStatistics[[attr(position, "DRB")]] <- mean(games[[attr(position, "DRB")]]);
    teamStatistics[[attr(position, "TRB")]] <- mean(games[[attr(position, "TRB")]]);
    teamStatistics[[attr(position, "PTS1")]] <- mean(games[[attr(position, "PTS1")]]);
    teamStatistics[[attr(position, "PTS2")]] <- mean(games[[attr(position, "PTS2")]]);
    teamStatistics[[attr(position, "PTS3")]] <- mean(games[[attr(position, "PTS3")]]);
    teamStatistics[[attr(position, "PTS4")]] <- mean(games[[attr(position, "PTS4")]]);
    teamStatistics[[attr(position, "PTSEx")]] <- mean(games[[attr(position, "PTSEx")]]);
    
    # percetanges
    teamStatistics[[attr(position, "FGR")]] <- mean(games[[attr(position,"FGM")]]) / mean(games[[attr(position, "FGA")]]);
    teamStatistics[[attr(position, "3PR")]] <- mean(games[[attr(position,"3PM")]]) / mean(games[[attr(position, "3PA")]]);
    teamStatistics[[attr(position, "2PR")]] <- mean(games[[attr(position,"2PM")]]) / mean(games[[attr(position, "2PA")]]);
    teamStatistics[[attr(position, "FTR")]] <- mean(games[[attr(position,"FTM")]]) / mean(games[[attr(position, "FTA")]]);

    return(teamStatistics);
}

winsInGamesBefore <- function (teamAbbr, beforeDate, data) {
    homeGamesSelection <- data$homeAbbr == teamAbbr & data$gmDate < beforeDate;
    homeGames <- data[homeGamesSelection,];
    homeWinsRatio <- sum(homeGames$homePTS > homeGames$awayPTS) / length(homeGames$homePTS);
    awayGamesSelection <- data$awayAbbr == teamAbbr & data$gmDate < beforeDate;
    awayGames <- data[awayGamesSelection,];
    awayWinsRatio <- sum(awayGames$homePTS < awayGames$awayPTS) / length(awayGames$awayPTS);
    return (homeWinsRatio + awayWinsRatio) / 2;
}
# slice md to only 3000 games

# strukturiraj podatke za ucenje
structuredData <- data.frame();
for (i in 1:nrow(md)) {
    game <- md[i,];
    homeTeamGamesSelection <- md$homeAbbr == game$homeAbbr & md$gmDate < game$gmDate;
    homeTeamGames <- md[homeTeamGamesSelection,];
    if (nrow(homeTeamGames) == 0) {
        next;
    }

    structuredHomeTeamData = structureTeamData(homeTeamGames, "home");
    structuredHomeTeamData$homeWins <- winsInGamesBefore(game$homeAbbr, game$gmDate, md);


    awayTeamGamesSelection <- md$awayAbbr == game$awayAbbr & md$gmDate < game$gmDate;
    awayTeamGames <- md[awayTeamGamesSelection,];
    if (nrow(awayTeamGames) == 0) {
        next;
    }
    structuredAwayTeamData = structureTeamData(awayTeamGames, "away");
    structuredAwayTeamData$awayWins <- winsInGamesBefore(game$awayAbbr, game$gmDate, md);
    
    structuredGameData <- c(structuredHomeTeamData, structuredAwayTeamData);
    structuredGameData$isHomeWinner <- game$homePTS > game$awayPTS;
    structuredData <- rbind(structuredData, structuredGameData);
}

structuredData$isHomeWinner <- as.factor(structuredData$isHomeWinner);
# GainRatio omili precenjevanje vecvrednostih attributov
informationGain <- sort(attrEval(isHomeWinner ~ ., structuredData, "ReliefFequalK"), decreasing = TRUE)
# na podlagi analize atributov sva odstranila PTSEx, TO, DayOff, 


HOME_TEAM_TO_PREDICT <- "GSW";
AWAY_TEAM_TO_PREDICT <- "CLE";












