

library(CORElearn)

# HELPERS
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

    teamStatistics[[attr(position, "ORBR")]] <- mean(games[[attr(position, "ORB")]] / games[[attr(position, "TRB")]]);
    teamStatistics[[attr(position, "DRBR")]] <- mean(games[[attr(position, "DRB")]] / games[[attr(position, "TRB")]]);
    
    # percetanges
    teamStatistics[[attr(position, "FGR")]] <- mean(games[[attr(position,"FGM")]] / games[[attr(position, "FGA")]]);
    teamStatistics[[attr(position, "3PR")]] <- mean(games[[attr(position,"3PM")]] / games[[attr(position, "3PA")]]);
    teamStatistics[[attr(position, "2PR")]] <- mean(games[[attr(position,"2PM")]] / games[[attr(position, "2PA")]]);
    teamStatistics[[attr(position, "FTR")]] <- mean(games[[attr(position,"FTM")]] / games[[attr(position, "FTA")]]);

    return(teamStatistics);
}

pastWinLoseRatio <- function (teamAbbr, beforeDate, data) {
    homeGamesSelection <- data$homeAbbr == teamAbbr & data$gmDate < beforeDate;
    homeGames <- data[homeGamesSelection,];
    homeWinsRatio <- mean(homeGames$homePTS > homeGames$awayPTS);
    awayGamesSelection <- data$awayAbbr == teamAbbr & data$gmDate < beforeDate;
    awayGames <- data[awayGamesSelection,];
    if (nrow(homeGames) == 0 && nrow(awayGames) == 0) {
        return(0);
    }
    awayWinsRatio <- mean(awayGames$homePTS < awayGames$awayPTS);
    if (is.na(homeWinsRatio)) {
        return(awayWinsRatio);
    }
    if (is.na(awayWinsRatio)) {
        return(homeWinsRatio);
    }
    return ((homeWinsRatio + awayWinsRatio) / 2);
}

# vrne pozitiven rezultat ce ima ekipa1 overal vec tock , negativen pa ce jih ima ekipa2 vec
pastMatchesScoreDifference <- function (team1, team2, beforeDate) {
    homeGamesSelection <- md$homeAbbr == team1 & md$awayAbbr == team2 & md$gmDate < beforeDate;
    homeGames <- md[homeGamesSelection,];
    homeGamesScoreDifference <- sum(homeGames$homePTS - homeGames$awayPTS);
    awayGamesSelection <- md$homeAbbr == team2 & md$awayAbbr == team1 & md$gmDate < beforeDate;
    awayGames <- md[awayGamesSelection,];
    awayGamesScoreDifference <- sum(awayGames$awayPTS - awayGames$homePTS);
    totalSum = homeGamesScoreDifference + awayGamesScoreDifference;
    if (is.null(totalSum) || is.na(totalSum)) {
        return(0);
    }
    return (totalSum);
}


structuredData <- data.frame();
for (i in 1:nrow(md)) {
    game <- md[i,];
    homeTeamGamesSelection <- md$homeAbbr == game$homeAbbr & md$gmDate < game$gmDate;
    homeTeamGames <- md[homeTeamGamesSelection,];
    if (nrow(homeTeamGames) == 0) {
        next;
    }
    structuredHomeTeamData = structureTeamData(homeTeamGames, "home");

    awayTeamGamesSelection <- md$awayAbbr == game$awayAbbr & md$gmDate < game$gmDate;
    awayTeamGames <- md[awayTeamGamesSelection,];
    if (nrow(awayTeamGames) == 0) {
        next;
    }
    structuredAwayTeamData = structureTeamData(awayTeamGames, "away");
    
    # zdruzeno v vrstico
    structuredGameData <- c(structuredHomeTeamData, structuredAwayTeamData);

    # dodamo nove atribute
    structuredGameData$homeWins <- pastWinLoseRatio(game$homeAbbr, game$gmDate, md);
    structuredGameData$awayWins <- pastWinLoseRatio(game$awayAbbr, game$gmDate, md);
    structuredGameData$pastMatchesScoreDifference <- pastMatchesScoreDifference(game$homeAbbr, game$awayAbbr, game$gmDate);
    structuredGameData$isHomeWinner <- game$homePTS > game$awayPTS;
    structuredGameData$scoreDifference <- game$homePTS - game$awayPTS;

    structuredData <- rbind(structuredData, structuredGameData);
}



# <DEBUG>
# structuredData <- read.csv("my_file.csv");
print(colnames(structuredData));
# </DEBUG>

structuredData$isHomeWinner <- as.factor(structuredData$isHomeWinner);

# KLASIFIKACIJA

splitData = Split70to30(structuredData);
train <- splitData[[1]];
test <- splitData[[2]];


# EVALUATION HELPERS
# odstotek pravilno klasificiranih pozitivnih primerov
Sensitivity <- function(obs, pred, pos.class)
{
    TP <- sum(obs == pos.class & pred == pos.class)
    FN <- sum(obs == pos.class & pred != pos.class)
    return(TP / (TP + FN))
}
# odstotek pravilno klasificiranih negativnih primerov
Specificity <- function(obs, pred, pos.class)
{
    TN <- sum(obs != pos.class & pred != pos.class)
    FP <- sum(obs != pos.class & pred == pos.class)
    return(TN / (TN + FP))
}
# odstotek pravilno klasificiranih primerov, ki so bili klasificirani kot pozitivni
Precision <- function(obs, pred, pos.class)
{
    TP <- sum(obs == pos.class & pred == pos.class)
    FP <- sum(obs != pos.class & pred == pos.class)
    return(TP / (TP + FP))
}

CA <- function(observed, predicted)
{
	mean(observed == predicted)
}

brierScore <- function(observedMatrix, predictedMatrix)
{
	sum((observedMatrix - predictedMatrix) ^ 2) / nrow(predictedMatrix)
}
library(nnet)
obsMat <- class.ind(test$isHomeWinner)
observed <- test$isHomeWinner;


# DECISION TREE
# library(rpart)
# library(rpart.plot)
# dt <- rpart(isHomeWinner ~ . - scoreDifference, data=train, cp=0)
# rpart.plot(dt)

# # rpart med gradnjo drevesa interno ocenjuje njegovo kvaliteto 
# printcp(dt)
# tab <- printcp(dt)

# # izberemo vrednost parametra cp, ki ustreza minimalni napaki internega presnega preverjanja
# row <- which.min(tab[,"xerror"])
# th <- mean(c(tab[row, "CP"], tab[row-1, "CP"]))
# th

# # porezemo drevo z izbrano nastavitvijo
# dt <- prune(dt, cp=th)
# rpart.plot(dt)

# predicted <- predict(dt, test, type="class")
# CA(observed, predicted)
# Sensitivity(observed, predicted, "TRUE")
# Specificity(observed, predicted, "TRUE")
# Precision(observed, predicted, "TRUE")
# predMat <- predict(dt, test, type = "prob")
# brierScore(obsMat, predMat)


# NAIVNI BAYESOV KLASIFIKATOR

# library(e1071)

# nb <- naiveBayes(isHomeWinner ~ . - scoreDifference, data = train)
# predicted <- predict(nb, test, type="class")


# CA(observed, predicted)
# Sensitivity(observed, predicted, "TRUE")
# Specificity(observed, predicted, "TRUE")
# Precision(observed, predicted, "TRUE")
# predMat <- predict(nb, test, type = "prob")
# brierScore(obsMat, predMat)


# # RANDOM FOREST

# library(randomForest)
# rf <- randomForest(isHomeWinner ~ . - scoreDifference, data = train)
# predicted <- predict(rf, test, type="class")
# CA(observed, predicted)

# predMat <- predict(rf, test, type = "prob")
# brier.score(obsMat, predMat)
# CA(observed, predicted)
# Sensitivity(observed, predicted, "TRUE")
# Specificity(observed, predicted, "TRUE")
# Precision(observed, predicted, "TRUE")
# predMat <- predict(rf, test, type = "prob")
# brierScore(obsMat, predMat)


# # REGRESIJA

# mere ocenjevanja
# srednja absolutna napaka
mae <- function(obs, pred)
{
    obs <- as.numeric(obs);
    pred <- as.numeric(pred);
	mean(abs(obs - pred))
}

# srednja kvadratna napaka
mse <- function(obs, pred)
{
    obs <- as.numeric(obs);
    pred <- as.numeric(pred);
	mean((obs - pred)^2)
}
# relativna srednja absolutna napaka
rmae <- function(obs, pred, mean.val) 
{  
    obs <- as.numeric(obs);
    pred <- as.numeric(pred);
	sum(abs(obs - pred)) / sum(abs(obs - mean.val))
}

# relativna srednja kvadratna napaka
rmse <- function(obs, pred, mean.val) 
{
    obs <- as.numeric(obs);
    pred <- as.numeric(pred);
	sum((obs - pred)^2)/sum((obs - mean.val)^2)
}


splitData = Split70to30(structuredData);
train <- splitData[[1]];
test <- splitData[[2]];


#
# Trivialni model
#
# meanVal <- mean(train$scoreDifference);
# meanVal

# predTrivial <- rep(meanVal, nrow(test))
# mae(observed, predTrivial)
# mse(observed, predTrivial)


# Precno preverjanje
predicted <- vector()

for (i in 1:nrow(structuredData))
{	
    # linearna regresija
	model <- lm(scoreDifference ~ . - isHomeWinner, structuredData[-i,])
	predicted[i] <- predict(model, structuredData[i,])
}

plot(train$scoreDifference)
points(predicted, col="red")

mae(structuredData$scoreDifference, predicted)
mse(structuredData$scoreDifference, predicted)

rmae(structuredData$scoreDifference, predicted, mean(structuredData$scoreDifference))
rmse(structuredData$scoreDifference, predicted, mean(structuredData$scoreDifference))

# print("Done")









